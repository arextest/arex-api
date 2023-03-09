package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import lombok.Data;

import java.util.List;

@Data
public class DiffMsgWithCategoryDetail {
    private String id;
    private String operationName;
    private int diffResultCode;

    private List<LogEntity> logs;
    private String baseMsg;
    private String testMsg;
}