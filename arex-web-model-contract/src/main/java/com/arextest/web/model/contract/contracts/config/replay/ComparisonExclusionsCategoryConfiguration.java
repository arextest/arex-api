package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:39
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComparisonExclusionsCategoryConfiguration extends AbstractComparisonDetailsConfiguration {
    List<String> exclusionsCategory;
    List<String> candidateCategories;

    @Override
    public void validParameters() throws Exception {
        super.validParameters();
        if (CollectionUtils.isEmpty(exclusionsCategory)) {
            throw new Exception("exclusionsCategory cannot be empty");
        }
    }
}
