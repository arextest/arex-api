package com.arextest.report.model.api.contracts.configservice.replay;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
public class ComparisonListSortConfiguration extends AbstractComparisonDetailsConfiguration {
    @NonNull
    private List<String> listPath;
    @NonNull
    private List<List<String>> keys;
}
