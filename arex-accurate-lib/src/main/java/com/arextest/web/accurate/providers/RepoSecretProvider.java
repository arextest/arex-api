package com.arextest.web.accurate.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Qzmo on 2023/7/19
 */
@Component
public class RepoSecretProvider {
    @Value("${github.token}")
    private String githubToken;

    @Value("${gitlab.token}")
    private String gitlabToken;

    public String getGithubToken() {
        return githubToken;
    }

    public String getGitlabToken() {
        return gitlabToken;
    }
}
