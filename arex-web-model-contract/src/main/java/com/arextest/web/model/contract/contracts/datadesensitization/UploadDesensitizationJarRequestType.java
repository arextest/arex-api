package com.arextest.web.model.contract.contracts.datadesensitization;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UploadDesensitizationJarRequestType {

  @NotBlank(message = "Jar url can not be empty")
  private String jarUrl;
  private String remark;
}
