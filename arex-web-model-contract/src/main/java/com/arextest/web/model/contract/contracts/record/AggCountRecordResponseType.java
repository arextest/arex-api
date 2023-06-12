package com.arextest.web.model.contract.contracts.record;

import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import lombok.Data;

import java.util.List;

@Data
public class AggCountRecordResponseType {
    List<ApplicationOperationConfiguration> operationList;
}
