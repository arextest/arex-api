package com.arextest.web.accurate.model;

import lombok.Data;

import java.util.List;

@Data
public class BaseDiffReq {
    private int hostType;
    private String repoPath;
    private String newCommitSha;
    private String oldCommitSha;
    // private String repositoryURL;
    // private String branch;
    // private List<String> classes;
    // private List<String> methods;
    // private List<String> interfaces;

    public CodeHostType getHostEnum() {
        return CodeHostType.fromCode(this.hostType);
    }
}

