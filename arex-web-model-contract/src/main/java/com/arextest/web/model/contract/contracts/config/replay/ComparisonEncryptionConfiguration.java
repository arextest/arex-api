package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
