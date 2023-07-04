package com.arextest.accurate.model;

import lombok.Data;

import java.util.List;

@Data
public class TracingResponse {
    private int statusCode = 0;
    private String result;
    private Object data;
    private List<MethodTracing> messages;

    public static TracingResponse exceptionResponse(String errors){
        TracingResponse tracingResponse = new TracingResponse();
        tracingResponse.setStatusCode(-1);
        tracingResponse.setResult(errors);
        return tracingResponse;
    }
}
