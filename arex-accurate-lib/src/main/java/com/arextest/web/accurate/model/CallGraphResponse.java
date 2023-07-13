package com.arextest.web.accurate.model;

import lombok.Data;

import java.util.List;

@Data
public class CallGraphResponse {
    private int errorCode = 0;
    private String result;
    private List<MethodTracing> stacks;

    public static CallGraphResponse exceptionResponse(String exceptionInfo){
        CallGraphResponse callGraphResponse = new CallGraphResponse();
        callGraphResponse.setErrorCode(-1);
        callGraphResponse.setResult(exceptionInfo);
        return callGraphResponse;
    }
}
