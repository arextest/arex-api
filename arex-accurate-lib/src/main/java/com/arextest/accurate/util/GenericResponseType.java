package com.arextest.accurate.util;

public class GenericResponseType<T> implements Response {
    private ResponseStatusType responseStatusType;
    private T body;

    public ResponseStatusType getResponseStatusType() {
        return this.responseStatusType;
    }

    public T getBody() {
        return this.body;
    }

    public void setResponseStatusType(ResponseStatusType responseStatusType) {
        this.responseStatusType = responseStatusType;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public GenericResponseType() {
    }

    public GenericResponseType(ResponseStatusType responseStatusType, T body) {
        this.responseStatusType = responseStatusType;
        this.body = body;
    }
}