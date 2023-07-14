/***
 * https://github.com/gocd/gocd/blob/cc84e2045638e8afca741a016f4c02e727efe807/config/config-server/src/main/java/com/thoughtworks/go/service/ConfigRepository.java#L138:39
 */
package com.arextest.web.accurate.lib;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Collections.min;

@Slf4j
@Data
public class JGitRepository {
    private Git git;
    private Repository gitRepo;
    private String gitDir;
    private String gitRepositoryURL;
    private String gitBranch;

    public static String getRepositoryDir(String repositoryURL) {
        return repositoryURL.substring(repositoryURL.lastIndexOf("/")).replace(".git", "");
    }

    public JGitRepository(String repositoryURL, String rootDir, String branch) {
        gitRepositoryURL = repositoryURL;
        gitDir = rootDir;
        gitBranch = branch;
    }

    /**
     * 初始化代码仓库, pull最新代码
     * 如果传入了commitid,就是更新到commit
     *
     * @return 返回初始化是否失败
     * @throws IOException
     */
    public boolean buildJGitRepository(String userName, String token) {
        try {
            File configRepoDir = new File(gitDir, ".git");
            if (!configRepoDir.exists()) {
                cloneRepositoryShell(gitDir, userName, token);
            }

            gitRepo = new FileRepositoryBuilder().setGitDir(configRepoDir).build();
            git = new Git(gitRepo);
            git.pull();
            return true;
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return false;
        }
    }


    private RevCommit commitChanges() throws GitAPIException {
        git.add().addFilepattern(".").call();
        return git.commit().setMessage("commit message").call();
    }

    private static void writeFile(File file, String content) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(content.getBytes("UTF-8"));
        outputStream.close();
    }

    public interface ThrowingFn<T, E extends Exception> {
        T call() throws E, GitAPIException;
    }

    public <T, E extends Exception> T doLocked(ThrowingFn<T, E> runnable) throws E, GitAPIException {
        synchronized (this) {
            return runnable.call();
        }
    }

    public String findChangesForCommits(final String fromRevision, final String toRevision) throws GitAPIException {
        return doLocked(() -> {
            RevCommit laterCommit = null;
            RevCommit earlierCommit = null;
            if (!StringUtils.isBlank(fromRevision)) {
                laterCommit = getRevCommitForCommitSHA(fromRevision);
            }
            if (!StringUtils.isBlank(toRevision)) {
                earlierCommit = getRevCommitForCommitSHA(toRevision);
            }
            return findDiffBetweenTwoRevisions(laterCommit, earlierCommit);
        });
    }

    /**
     * 获取变更的一种方式, 暂时保留代码
     * (Repository repo = Git.open(Paths.get("https://github.com/arextest/arex-report.git").toFile()).getRepository())
     *
     * @param fromRevision
     * @param toRevision
     * @return
     */
    public HashSet<String> findFilesBetweenCommits(final String fromRevision, final String toRevision) {
        try {
            ObjectId oldObject = ObjectId.fromString(fromRevision);
            ObjectId newObject = ObjectId.fromString(toRevision);
            Repository repo = git.getRepository();
            HashSet<String> modifiedFiles = new HashSet<>();

            try (ObjectReader reader = repo.newObjectReader();
                 RevWalk revWalk = new RevWalk(repo)) {
                RevCommit oldCommit = revWalk.parseCommit(oldObject);
                RevCommit newCommit = revWalk.parseCommit(newObject);

                CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                oldTreeParser.reset(reader, oldCommit.getTree().getId());

                CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
                newTreeParser.reset(reader, newCommit.getTree().getId());

                try (DiffFormatter formatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    formatter.setRepository(repo);
                    formatter.setDiffComparator(RawTextComparator.DEFAULT);
                    formatter.setDetectRenames(true);

                    List<DiffEntry> diffs = formatter.scan(oldTreeParser, newTreeParser);

                    for (DiffEntry entry : diffs) {
                        if (entry.getChangeType() == DiffEntry.ChangeType.MODIFY) {
                            if (!entry.getNewPath().endsWith(".java"))
                                continue;

                            List<Edit> edits = formatter.toFileHeader(entry).toEditList();
                            for (Edit edit : edits) {
                                int beginA = edit.getBeginA();
                                int endA = edit.getEndA();
                                int beginB = edit.getBeginB();
                                int endB = edit.getEndB();

                                for (int i = beginB; i < endB; i++) {
                                    if (i >= beginA && i < endA) {
                                        System.out.println("Function modified: " + entry.getOldPath() + ", lines " + (i + 1));
                                        modifiedFiles.add(entry.getOldPath());
                                    } else {
                                        System.out.println("Function added: " + entry.getNewPath() + ", lines " + (i + 1));
                                    }
                                }

                                for (int i = beginA; i < endA; i++) {
                                    if (i >= beginB && i < endB) {
                                        // Function was already printed as modified
                                    } else {
                                        System.out.println("Function removed: " + entry.getOldPath() + ", lines " + (i + 1));
                                    }
                                }
                            }
                        }
                    }
                    return modifiedFiles;
                } catch (Exception e) {
                    LOGGER.info(e.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.info(e.toString());
        }

        return null;
    }

    public Iterable<RevCommit> revisions() throws GitAPIException {
        LogCommand command = git.log();
        return command.call();
    }

    RevCommit getRevCommitForCommitSHA(String commitSHA) throws GitAPIException {
        for (RevCommit revision : revisions()) {
            if (revision.getName().equals(commitSHA)) {
                return revision;
            }
        }
        throw new IllegalArgumentException(String.format("There is no commit corresponding to SHA: '%s'", commitSHA));
    }

    public static String stripTillLastOccurrenceOf(String input, String pattern) {
        if (!StringUtils.isBlank(input) && !StringUtils.isBlank(pattern)) {
            int index = input.lastIndexOf(pattern);
            if (index > 0) {
                input = input.substring(index + pattern.length());
            }
        }
        return input;
    }

    String findDiffBetweenTwoRevisions(RevCommit laterCommit, RevCommit earlierCommit) {
        if (laterCommit == null || earlierCommit == null) {
            return null;
        }
        String output = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DiffFormatter diffFormatter = new DiffFormatter(out);
            diffFormatter.setRepository(gitRepo);
            diffFormatter.format(earlierCommit.getId(), laterCommit.getId());
            output = out.toString();
            output = stripTillLastOccurrenceOf(output, "");
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during diff computation. Message: " + e.getMessage());
        }
        return output;
    }

    /**
     * find diff entry between two revisions.
     * 对于两个 commit，使用 JGit 获取它们的 GitCommit 对象。
     * 使用 JGit 中的 DiffCommand 比较这两个 commit，得到它们之间的差异。
     * 对于每个修改的文件，使用 JGit 获取它们的差异 DiffEntry。
     * 对于每个差异 DiffEntry，如果它的 ChangeType 是 MODIFY, 获取
     * HashMap<String, CodeDiff>获取的是文件名+变更行信息的清单
     * 将所有修改的方法名称加入到一个集合中，最终返回这个集合。
     *
     * @param laterCommit   later commit
     * @param earlierCommit first commit
     * @return hashmap
     */
    public HashMap<String, FileDiffContent> findDiffEntryBetweenTwoRevisions(RevCommit laterCommit, RevCommit earlierCommit) {
        try {
            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(gitRepo);
            List<DiffEntry> entries = diffFormatter.scan(earlierCommit.getId(), laterCommit.getId());
            if (entries.size() == 0) {
                return null;
            }
            HashMap<String, FileDiffContent> diffs = new HashMap<>();
            for (DiffEntry diff : entries) {
                if (!diff.getNewPath().endsWith(".java"))
                    continue;
                if (diff.getChangeType() != DiffEntry.ChangeType.MODIFY)
                    continue;

                String fullName = diff.getNewPath();
                String fileName = fullName.substring(fullName.lastIndexOf("/") + 1);
                FileDiffContent diffContent = new FileDiffContent(fileName, fullName);
                // 获取新版本文件的ObjectID
                ObjectId newFileId = diff.getNewId().toObjectId();
                // 获取新版本文件的ObjectReader
                ObjectReader reader = git.getRepository().newObjectReader();
                // 通过ObjectReader获取文件的byte数组
                byte[] bytes = reader.open(newFileId, Constants.OBJ_BLOB).getBytes();
                // 将byte数组转换为字符串
                diffContent.setContent(new String(bytes));

                ObjectId oldFileId = diff.getOldId().toObjectId();
                byte[] oldBytes = reader.open(oldFileId, Constants.OBJ_BLOB).getBytes();
                diffContent.setOldContent(new String(oldBytes));

                RawText oldText = new RawText(oldBytes);
                RawText newText = new RawText(bytes);
                for (Edit edit : diffFormatter.toFileHeader(diff).toEditList()) {
                    StringBuilder sb = new StringBuilder();
                    if (edit.getType() != Edit.Type.DELETE) {
                        for (int i = edit.getBeginB(); i < edit.getEndB(); i++) {
                            if (edit.getType() == Edit.Type.INSERT) {
                                sb.append(" +").append(newText.getString(i)).append("\n");
                            } else {
                                sb.append(" +").append(newText.getString(i)).append("\n");
                                sb.append(" *").append(oldText.getString(i)).append("\n");
                            }
                            //这里可以增加无效的判断,比如全部是空格, 修改的是注释,设置标志位判断是否都是无效,都是无效则抛弃
                        }
                    }

                    diffContent.appendRange(edit.getBeginB(), edit.getEndB(), sb.toString());
                }

                diffs.put(fullName, diffContent);
            }
            return diffs;
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during diff computation. Message: " + e.getMessage());
        }
    }

    /**
     * git pull
     *
     * @return true:success
     * TODO: 问题Pull暂时不管分支.setRemoteBranchName(gitBranch)
     */
    public boolean pullRepository(String userName, String password) {
        UsernamePasswordCredentialsProvider provider = new
                UsernamePasswordCredentialsProvider(userName, password);

        try {
            Repository repository = new FileRepository(gitDir + "/.git");
            Git git = new Git(repository);
            git.pull().setRebase(true)
                    .setCredentialsProvider(provider)
                    .call();
            return true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
        return false;
    }

    /**
     * clone git repository by shell
     *
     * @param rootDir repository root dir
     * @param uname   git user name
     * @param pwd     git token
     * @return result description
     */
    public String cloneRepositoryShell(String rootDir, String uname, String pwd) {
        String url = gitRepositoryURL.replace("http://", String.format("http://%s:%s@", uname, pwd));
        String command = String.format("git clone -b %s %s", gitBranch, url);
        try {
            StringBuilder strLogs = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();
            Process pro = runtime.exec(command, null, new File(rootDir + "/.."));
            int status = pro.waitFor();
            strLogs.append(command);
            if (status != 0) {
                strLogs.append("\r\nFailed to call shell's command. Status: " + status);
            } else
                strLogs.append("\r\nClone success. status:" + status);

            BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                strLogs.append("\r\n").append(line);
            }
            return strLogs.toString();
        } catch (IOException ec) {
            ec.printStackTrace();
            return "IOException:" + ec.toString();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return "InterruptedException" + ex.toString();
        }
    }

    /**
     * TODO: API Clone Failed
     *
     * @param uname
     * @param pwd
     * @return
     */
    public String cloneRepositoryByAPI(String uname, String pwd) {
        File codeNewGitDir = new File(gitDir);
        if (codeNewGitDir.exists()) {
            return gitDir + " exists. clone failed.";
        }
        Git gitClone = null;
        try {
            if (StringUtils.isEmpty(uname)) {
                gitClone = Git.cloneRepository()
                        .setURI(gitRepositoryURL)
                        .setDirectory(codeNewGitDir)
                        .setBranch(gitBranch)
                        .call();
            } else {
                UsernamePasswordCredentialsProvider provider = new
                        UsernamePasswordCredentialsProvider(uname, pwd);

                gitClone = Git.cloneRepository()
                        .setCredentialsProvider(provider)
                        .setURI(gitRepositoryURL)
                        .setDirectory(codeNewGitDir)
                        .setCloneSubmodules(false)
//                        .setBranchesToClone(singleton(gitBranch))
                        .setBranch(gitBranch)
                        .call();
            }
            gitClone.getRepository().close();
            gitClone.close();
            return "success";
        } catch (GitAPIException e) {
            e.printStackTrace();
            String err = "Git.cloneRepository() failed: " + e;
            if (gitClone != null) {
                gitClone.close();
            }
            LOGGER.error(err);
            return err;
        }
    }

    /**
     * 获取某个commitID上的所有文件
     *
     * @param repository
     * @param commitId
     * @throws IOException
     */
    public void walkFilesInCommit(Repository repository, String commitId) throws IOException {
        try (Git git = new Git(repository)) {
            RevCommit commit = getRevCommitForCommitSHA(commitId);
            ObjectReader reader = git.getRepository().newObjectReader();

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(reader, commit.getTree());

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(treeParser);
            treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                if (treeWalk.isSubtree())
                    continue;
                String filePath = treeWalk.getPathString();
                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);
                byte[] contentBytes = loader.getBytes();
                String content = new String(contentBytes, StandardCharsets.UTF_8);
                System.out.println(filePath);
            }
            treeWalk.close();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * reset到指定的commit
     *
     * @param commitId
     */
    public void checkoutByCommit(String commitId) {
        try {
            CheckoutCommand checkout = git.checkout();
            // 设置要切换到的 commit
            ObjectId commitObjectId = git.getRepository().resolve(commitId);
            checkout.setStartPoint(commitObjectId.getName());
            // 执行切换操作
            checkout.call();
            System.out.println("Repository updated to commit: " + commitId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TEST CODE
     *
     * @throws IOException
     * @throws GitAPIException
     */
    public void getDiffDemo() throws IOException, GitAPIException {
        File file = new File(git.getRepository().getWorkTree(), "ab.txt");
        writeFile(file, "line1\n");
        RevCommit oldCommit = commitChanges();
        writeFile(file, "line1\nline2\n");
        RevCommit newCommit = commitChanges();

        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldCommit.getTree());
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, newCommit.getTree());

        DiffFormatter diffFormatter = new DiffFormatter(System.out);
        diffFormatter.setRepository(git.getRepository());
        List<DiffEntry> entries = diffFormatter.scan(newTreeIter, oldTreeIter);
        diffFormatter.format(entries);
        diffFormatter.close();
    }


}
