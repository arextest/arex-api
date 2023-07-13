package com.arextest.web.accurate.model;

import lombok.Data;

import java.util.List;

@Data
public class NormalGitDiffRequest {
    private String repositoryURL;
    private String branch;
    private String newCommit;
    private String oldCommit;
    private List<String> classes;
    private List<String> methods;
    private List<String> interfaces;
}
