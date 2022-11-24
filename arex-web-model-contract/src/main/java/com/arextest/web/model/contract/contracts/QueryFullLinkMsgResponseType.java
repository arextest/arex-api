package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.CompareResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryFullLinkMsgResponseType {
    List<CompareResult> compareResults;
}
