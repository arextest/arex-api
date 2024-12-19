package com.arextest.web.model.dto.config;

import lombok.Data;

/**
 * @author b_yu
 * @since 2024/12/16
 */

@Data
public class PageQueryListSortDto extends PageQueryComparisonDto {
  private String keyOfListPath;
  private String keyOfValue;
}
