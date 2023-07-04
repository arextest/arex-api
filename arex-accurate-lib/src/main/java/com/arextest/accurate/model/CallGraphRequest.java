package com.arextest.accurate.model;

import lombok.Data;

@Data
public class CallGraphRequest {
    private String repositoryURL;
    private String branch;
    private String className;
    private String methodName;
}
