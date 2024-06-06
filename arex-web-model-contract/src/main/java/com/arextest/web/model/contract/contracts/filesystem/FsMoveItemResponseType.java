package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.SuccessResponseType;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wildeslam.
 * @create 2024/6/6 16:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FsMoveItemResponseType extends SuccessResponseType {

  private List<String> fromPath;
  private List<String> toPath;

}
