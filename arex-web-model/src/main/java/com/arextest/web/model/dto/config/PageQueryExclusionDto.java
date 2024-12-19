package com.arextest.web.model.dto.config;

import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/12/16
 */

@Data
public class PageQueryExclusionDto extends PageQueryComparisonDto {
  private String keyOfExclusionPath;
}
