package com.arextest.web.model.contract.contracts.record;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import java.util.List;
import lombok.Data;

@Data
public class AggCountRecordResponseType {

  List<ApplicationOperationConfiguration> operationList;
}
