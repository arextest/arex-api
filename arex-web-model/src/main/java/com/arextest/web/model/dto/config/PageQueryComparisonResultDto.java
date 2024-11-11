package com.arextest.web.model.dto.config;

import java.util.List;
import lombok.Data;

@Data
public class PageQueryComparisonResultDto<T> {

  Long totalCount;

  List<T> configs;
}
