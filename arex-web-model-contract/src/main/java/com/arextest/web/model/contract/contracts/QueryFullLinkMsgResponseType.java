package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.CompareResult;

import lombok.Data;

@Data
public class QueryFullLinkMsgResponseType extends DesensitizationResponseType {
    List<CompareResult> compareResults;
}
