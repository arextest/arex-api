package com.arextest.accurate.model;

import com.arextest.accurate.lib.CodeDiff;
import com.arextest.accurate.util.Response;
import com.arextest.accurate.util.ResponseStatusType;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;

//@Builder
@Data
public class GitBasicResponse implements Response {
    private int errorCode;
    private String result;
    private Object data = null;

    private String newCommit;
    private String oldCommit;
    private HashMap<String, CodeDiff> diffs;

    public static GitBasicResponse exceptionResponse(String description) {
        GitBasicResponse gitBasicResponse = new GitBasicResponse();
        gitBasicResponse.setErrorCode(-1);
        gitBasicResponse.setResult(description);
        return gitBasicResponse;
    }

    @Override
    public ResponseStatusType getResponseStatusType() {
        return null;
    }

    @Override
    public void setResponseStatusType(ResponseStatusType var1) {

    }
}
