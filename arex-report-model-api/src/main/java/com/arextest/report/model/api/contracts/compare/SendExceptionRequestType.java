package com.arextest.report.model.api.contracts.compare;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2022/7/8.
 */
@Data
public class SendExceptionRequestType {
    private List<ExceptionMsg> exceptionMsgs;
}
