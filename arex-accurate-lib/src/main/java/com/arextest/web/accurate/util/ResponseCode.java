package com.arextest.web.accurate.util;

public enum ResponseCode {
    SUCCESS(0),
    REQUESTED_PARAMETER_INVALID(1),
    REQUESTED_HANDLE_EXCEPTION(2),
    REQUESTED_RESOURCE_NOT_FOUND(3),
    AUTHENTICATION_FAILED(4);

    private final int codeValue;

    private ResponseCode(int codeValue) {
        this.codeValue = codeValue;
    }

    public int getCodeValue() {
        return this.codeValue;
    }
}
