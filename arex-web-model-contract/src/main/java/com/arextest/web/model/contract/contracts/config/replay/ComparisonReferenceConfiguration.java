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
public class ComparisonReferenceConfiguration extends AbstractComparisonDetailsConfiguration {
    private List<String> pkPath;
    private List<String> fkPath;

    @Override
    public void validParameters() throws Exception {
        if (CollectionUtils.isEmpty(pkPath)) {
            throw new Exception("listPath cannot be empty");
        }
        if (CollectionUtils.isEmpty(fkPath)) {
            throw new Exception("keys cannot be empty");
        }
    }
}
