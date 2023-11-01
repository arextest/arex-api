package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.CompareResult;
import java.util.List;
import lombok.Data;

@Data
public class QueryFullLinkMsgResponseType extends DesensitizationResponseType {

  List<CompareResult> compareResults;
}
