package com.arextest.web.model.contract.contracts;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.response.Response;
import com.arextest.model.response.ResponseStatusType;
import java.util.List;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/10/16 16:40
 */
@Data
public class ViewRecordResponseType implements Response {

  private ResponseStatusType responseStatusType;
  private List<AREXMocker> recordResult;
  private boolean desensitized;
}
