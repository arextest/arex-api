package com.arextest.web.accurate.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class BaseDiffRes {
    private String repoPath;
    private String newCommitSha;
    private String oldCommitSha;
    private Map<String, Set<String>> modifiedMethods;
}
