package com.arextest.web.model.contract.contracts.replay;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by rchen9 on 2023/6/7.
 */
@Data
public class AnalyzeCompareResultsRequestType {
    @NotNull(message = "ids cannot be null")
    private List<String> ids;
}
