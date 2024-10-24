package com.arextest.web.model.contract.contracts.filesystem;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2024/6/6 17:06
 */
@Data
public class FSGetPathInfoRequestType {
  @NotBlank(message = "InfoId cannot be empty")
  private String infoId;
  @NotNull(message = "nodeType cannot be empty")
  @Min(value = 1, message = "nodeType must be greater than or equal to 1")
  @Max(value = 3, message = "nodeType must be less than or equal to 3")
  private Integer nodeType;
}
