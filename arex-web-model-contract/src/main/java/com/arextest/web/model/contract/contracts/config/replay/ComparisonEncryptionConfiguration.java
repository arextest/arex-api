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
    String methodName;
    @Override
    public void validParameters() throws Exception {
        super.validParameters();
        if (CollectionUtils.isEmpty(path) || methodName.isEmpty()) {
            throw new Exception("path or methodName cannot be empty");
        }
    }
}
