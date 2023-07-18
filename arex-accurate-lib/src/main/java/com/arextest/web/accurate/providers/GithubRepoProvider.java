package com.arextest.web.accurate.providers;

import com.arextest.web.accurate.util.HttpDownload;
import com.arextest.web.accurate.differs.MethodDiff;
import com.spotify.github.v3.clients.GitHubClient;
import com.spotify.github.v3.clients.RepositoryClient;
import com.spotify.github.v3.repos.Commit;
import com.spotify.github.v3.repos.Content;
import com.spotify.github.v3.repos.File;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Qzmo on 2023/7/18
 */
public class GithubRepoProvider {
    private static final GitHubClient githubClient = GitHubClient.create(URI.create("https://api.github.com/"), "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");
    private RepositoryClient repoClient;
    public GithubRepoProvider(String org, String repo) {
        repoClient = githubClient.createRepositoryClient(org, repo);
    }

    private CompletableFuture<List<File>> getCommitFiles(String sha) {
        return repoClient.getCommit(sha).thenApply(Commit::files);
    }

    private CompletableFuture<List<File>> getCommitDiffFiles(String base, String head) {
        return repoClient.compareCommits(base, head).thenApply(compare -> {
            return compare.files();
        });
    }

    private CompletableFuture<Content> findFile(String sha, String path) {
        return repoClient.getFileContent(path, sha);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        GithubRepoProvider gitHubProvider = new GithubRepoProvider("arextest", "arex-replay-schedule");
        String base = "206c434f09fc2348f42375988939720c3b0d82ce";
        String head = "dc5a2d19711d5b7db38eabad38e91b9d1705d902";

        List<File> modifiedFiles = gitHubProvider.getCommitDiffFiles(base, head).get();

        for (File modifiedFile : modifiedFiles) {
            Content oldFileContent = gitHubProvider.findFile(base, modifiedFile.filename()).get();
            // download uri to file
            String oldFile = HttpDownload.downloadFile(oldFileContent.downloadUrl(), oldFileContent.name());
            String newFile = HttpDownload.downloadFile(modifiedFile.rawUrl().get(), modifiedFile.filename());
            Set<String> diff = new MethodDiff().diff(oldFile, newFile);
            System.out.println("DONE");
        }
    }
}
