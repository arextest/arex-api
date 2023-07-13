package com.arextest.web.accurate.lib;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * implements WebMvcConfigurer
 */

@Component
public class GitConfig  {
    @Getter
    @Value("${code.workspace}")
    private String gitWorkspaceDir;

    @Getter
    @Value("${github.user}")
    private String gitUser;

    @Getter
    @Value("${github.token}")
    private String gitToken;

    @Getter
    @Value("${localhub.user}")
    private String gitLocalUser;

    @Getter
    @Value("${localhub.token}")
    private String gitLocalToken;
}
