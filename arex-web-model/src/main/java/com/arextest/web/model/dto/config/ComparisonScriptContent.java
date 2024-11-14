package com.arextest.web.model.dto.config;

import com.arextest.web.model.dto.BaseDto;
import lombok.Data;

@Data
public class ComparisonScriptContent extends BaseDto {

  private String aliasName;

  private String content;

}
