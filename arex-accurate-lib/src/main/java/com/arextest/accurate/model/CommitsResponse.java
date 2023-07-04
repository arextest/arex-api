package com.arextest.accurate.model;

import lombok.Data;

import java.util.List;

@Data
public class CommitsResponse {
    private int errorCode = 0;
    private String result;
    private List<CommitInfo> commits;

    public static CommitsResponse exceptionResponse(String exceptionInfo){
        CommitsResponse commitsResponse = new CommitsResponse();
        commitsResponse.setErrorCode(-1);
        commitsResponse.setResult(exceptionInfo);
        return commitsResponse;
    }
}
