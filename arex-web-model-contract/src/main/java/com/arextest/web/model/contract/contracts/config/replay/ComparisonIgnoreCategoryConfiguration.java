package com.arextest.web.model.contract.contracts.config.replay;

import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:39
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonIgnoreCategoryConfiguration extends AbstractComparisonDetailsConfiguration {
    List<String> ignoreCategory;

    @Override
    public void validParameters() throws Exception {
        super.validParameters();
        if (CollectionUtils.isEmpty(ignoreCategory)) {
            throw new Exception("ignoreCategory cannot be empty");
        }
    }
}
