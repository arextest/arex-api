package com.arextest.accurate.lib;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * implements WebMvcConfigurer
 */

@Configuration
@Component
public class GitConfig  {
    @Value("${info.workspace}")
    private String gitWorkspaceDir;

    public String getGitWorkspaceDir(){
        return gitWorkspaceDir;
    }

    @Value("${github.user}")
    private String gitUser;

    public String getGitUser(){
        return gitUser;
    }

    @Value("${github.token}")
    private String gitToken;

    public String getGitToken(){
        return gitToken;
    }

    @Value("${localhub.user}")
    private String gitLocalUser;

    public String getGitLocalUser(){
        return gitLocalUser;
    }

    @Value("${localhub.token}")
    private String gitLocalToken;

    public String getGitLocalToken(){
        return gitLocalToken;
    }
}
