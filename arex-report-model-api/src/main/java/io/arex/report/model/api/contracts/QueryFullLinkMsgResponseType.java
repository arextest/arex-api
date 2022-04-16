package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.CompareResult;
import lombok.Data;

import java.util.List;


@Data
public class QueryFullLinkMsgResponseType {
    List<CompareResult> compareResults;
}
