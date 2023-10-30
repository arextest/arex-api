package com.arextest.web.model.contract.contracts.config.replay;

import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonEncryptionConfiguration extends AbstractComparisonDetailsConfiguration {
    List<String> path;

    @Override
    public void validParameters() throws Exception {
        super.validParameters();
        if (CollectionUtils.isEmpty(path)) {
            throw new Exception("path cannot be empty");
        }
    }
}
