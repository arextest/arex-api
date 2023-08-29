package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:39
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonIgnoreCategoryConfiguration extends AbstractComparisonDetailsConfiguration {
    Set<String> ignoreCategory;

    @Override
    public void validParameters() throws Exception {
        super.validParameters();
        if (CollectionUtils.isEmpty(ignoreCategory)) {
            throw new Exception("ignoreCategory cannot be empty");
        }
    }
}
