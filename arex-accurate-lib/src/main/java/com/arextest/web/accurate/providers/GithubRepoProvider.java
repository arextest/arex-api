package com.arextest.web.accurate.providers;

import com.arextest.web.accurate.model.CodeHostType;
import com.arextest.web.accurate.model.JavaCodeFile;
import com.spotify.github.v3.clients.GitHubClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

/**
 * Created by Qzmo on 2023/7/18
 */
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
    public String getJavaCode(String projectPath, String filePath, String sha) {
        return null;
    }

    @Override
    public List<JavaCodeFile> getModifiedFiles(String projectPath, String oldCommit, String newCommit) {
        return null;
    }
}
