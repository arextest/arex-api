package com.arextest.web.accurate.model;

import com.arextest.web.accurate.lib.FileDiffContent;
import com.arextest.web.accurate.util.Response;
import com.arextest.web.accurate.util.ResponseStatusType;
import lombok.Data;

import java.util.HashMap;

//@Builder
@Data
public class GitBasicResponse implements Response {
    private int errorCode;
    private String result;
    private Object data = null;

    private String newCommit;
    private String oldCommit;
    private HashMap<String, FileDiffContent> diffs;

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
