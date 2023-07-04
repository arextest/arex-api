package com.arextest.accurate.util;

public final class ResponseUtils {
    private static final String REQUESTED_BODY_EMPTY = "requested body empty";
    private static final String SUCCESS = "success";

    private ResponseUtils() {
    }

    public static Response exceptionResponse(String remark) {
        return errorResponse(remark, ResponseCode.REQUESTED_HANDLE_EXCEPTION);
    }

    public static Response errorResponse(String remark, ResponseCode responseCode) {
        return errorResponse(responseStatus(remark, responseCode));
    }

    public static Response errorResponse(ResponseStatusType responseStatusType) {
        return create(responseStatusType, (Object)null);
    }

    public static <T> Response successResponse(T body) {
        return create(successStatus(), body);
    }

    public static <T> Response create(ResponseStatusType statusType, T body) {
        return new GenericResponseType(statusType, body);
    }

    public static Response resourceNotFoundResponse() {
        return errorResponse(ResponseCode.REQUESTED_RESOURCE_NOT_FOUND.name(), ResponseCode.REQUESTED_RESOURCE_NOT_FOUND);
    }

    public static Response parameterInvalidResponse(String remark) {
        return errorResponse(remark, ResponseCode.REQUESTED_PARAMETER_INVALID);
    }

    public static ResponseStatusType successStatus() {
        return responseStatus("success", ResponseCode.SUCCESS);
    }

    public static Response requestBodyEmptyResponse() {
        return parameterInvalidResponse("requested body empty");
    }

    private static ResponseStatusType responseStatus(String remark, ResponseCode responseCode) {
        ResponseStatusType responseStatusType = new ResponseStatusType();
        responseStatusType.setResponseDesc(remark);
        responseStatusType.setResponseCode(responseCode.getCodeValue());
        responseStatusType.setTimestamp(System.currentTimeMillis());
        return responseStatusType;
    }
}
