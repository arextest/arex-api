package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@NoArgsConstructor
public class ComparisonExclusionsConfiguration extends AbstractComparisonDetailsConfiguration {
    List<String> exclusions;

    @Override
    public void validParameters() throws Exception {
        if (CollectionUtils.isEmpty(exclusions)) {
            throw new Exception("exclusions cannot be empty");
        }
    }
}