package com.arextest.web.model.contract.contracts.record;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import lombok.Data;

import java.util.List;

@Data
public class AggCountRecordResponseType {
    List<ApplicationOperationConfiguration> operationList;
}
