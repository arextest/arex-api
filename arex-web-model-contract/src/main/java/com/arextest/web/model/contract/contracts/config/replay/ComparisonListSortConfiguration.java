package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Data
@NoArgsConstructor
public class ComparisonListSortConfiguration extends AbstractComparisonDetailsConfiguration {
    private List<String> listPath;
    private List<List<String>> keys;

    @Override
    public void validParameters() throws Exception {
        if (CollectionUtils.isEmpty(listPath)) {
            throw new Exception("listPath cannot be empty");
        }
        if (CollectionUtils.isEmpty(keys)) {
            throw new Exception("keys cannot be empty");
        }
    }
}
