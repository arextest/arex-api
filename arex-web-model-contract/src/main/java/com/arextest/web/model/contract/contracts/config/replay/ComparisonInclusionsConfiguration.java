package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@NoArgsConstructor
public class ComparisonInclusionsConfiguration extends AbstractComparisonDetailsConfiguration {
    List<String> inclusions;

    @Override
    public void validParameters() throws Exception {
        if (CollectionUtils.isEmpty(inclusions)) {
            throw new Exception("inclusions cannot be empty");
        }
    }
}