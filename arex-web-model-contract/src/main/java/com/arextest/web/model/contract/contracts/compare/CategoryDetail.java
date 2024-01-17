package com.arextest.web.model.contract.contracts.compare;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/1/5 15:45
 */
@Data
public class CategoryDetail {
    @EqualsAndHashCode.Include
    private String operationType;

    @EqualsAndHashCode.Include
    private String operationName;
}
