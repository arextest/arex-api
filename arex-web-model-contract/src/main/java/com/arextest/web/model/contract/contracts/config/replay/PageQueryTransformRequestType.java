package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;

/**
 * @author b_yu
 * @since 2024/12/16
 */
@Data
public class PageQueryTransformRequestType extends PageQueryComparisonRequestType {
  private String keyOfMethodName;
}
