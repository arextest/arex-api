package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.Difference;
import lombok.Data;

import java.util.List;


@Data
public class QueryDifferencesResponseType {

    private List<Difference> differences;
}
