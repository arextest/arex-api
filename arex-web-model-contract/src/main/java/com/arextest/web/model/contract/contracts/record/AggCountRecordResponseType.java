package com.arextest.web.model.contract.contracts.record;

import java.util.List;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;

import lombok.Data;

@Data
public class AggCountRecordResponseType {
    List<ApplicationOperationConfiguration> operationList;
}
