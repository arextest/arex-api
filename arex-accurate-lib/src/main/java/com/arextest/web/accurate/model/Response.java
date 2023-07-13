package com.arextest.web.accurate.model;

import lombok.Data;

@Data
public class Response {
    private int errorCode = 0;
    private String errors;
    private Object data;
    private String message;

    public static Response exceptionResponse(String exceptionInfo){
        Response response = new Response();
        response.setErrorCode(-1);
        response.setErrors(exceptionInfo);
        return response;
    }

    public static Response successResponse(){
        Response response = new Response();
        response.setErrors("");
        response.setMessage("success");
        return response;
    }

}
