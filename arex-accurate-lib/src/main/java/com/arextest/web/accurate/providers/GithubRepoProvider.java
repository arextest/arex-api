package com.arextest.web.accurate.providers;

import com.arextest.web.accurate.model.CodeHostType;
import com.arextest.web.accurate.model.JavaCodeFile;
import com.spotify.github.v3.clients.GitHubClient;
import com.spotify.github.v3.repos.Content;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Qzmo on 2023/7/18
 */
@Component
@Slf4j
public class GithubRepoProvider implements JavaCodeContentProvider {
    @Resource
    RepoSecretProvider repoSecretProvider;

    private GitHubClient githubClient;

    @PostConstruct
    public void init() {
         githubClient = GitHubClient.create(URI.create("https://api.github.com/"), repoSecretProvider.getGithubToken());
    }

    @Override
    public CodeHostType getCodeHostType() {
        return CodeHostType.GITHUB;
    }

    @Override
    public Optional<String> getJavaCode(String projectPath, String filePath, String sha) {
        // todo opt
        Pair<String, String> ownerAndProject = ownerAndProject(projectPath);
        try {
            Content res = githubClient.createRepositoryClient(ownerAndProject.getLeft(), ownerAndProject.getRight())
                    .getRepository().get().blobsUrl()
                    .getFileContent(filePath, sha).get();
            return Optional.ofNullable(res.content())
                    .map(source -> source.replace("\n", ""))
                    .map(source -> Base64.getDecoder().decode(source))
                    .map(String::new);
        } catch (Throwable t) {
            LOGGER.error("query github err", t);
            return Optional.empty();
        }
    }

    @Override
    public List<JavaCodeFile> getModifiedFiles(String projectPath, String oldCommit, String newCommit) {
        Pair<String, String> ownerAndProject = ownerAndProject(projectPath);
        try {
            return githubClient.createRepositoryClient(ownerAndProject.getLeft(), ownerAndProject.getRight())
                    .compareCommits(oldCommit, newCommit).get()
                    .files()
                    .stream()
                    .filter(fileDto -> StringUtils.isNotBlank(fileDto.filename())
                            && fileDto.filename().endsWith(".java"))
                    .map(fileDto -> {
                        JavaCodeFile fileBo = new JavaCodeFile();
                        fileBo.setFilePath(fileDto.filename());
                        return fileBo;
                    })
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            LOGGER.error("query github err", t);
            return null;
        }
    }

    private Pair<String, String> ownerAndProject(String projectPath) {
        String[] split = projectPath.split("/");
        return Pair.of(split[0], split[1]);
    }
}
