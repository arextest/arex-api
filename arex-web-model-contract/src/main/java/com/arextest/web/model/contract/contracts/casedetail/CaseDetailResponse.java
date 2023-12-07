package com.arextest.web.model.contract.contracts.casedetail;

import com.arextest.model.mock.Mocker;
import com.arextest.model.response.Response;
import com.arextest.model.response.ResponseStatusType;
import java.util.List;
import lombok.Data;

/**
 * Created by qzmo on 2023/12/6
 */
@Data
public class CaseDetailResponse implements Response {
  private ResponseStatusType responseStatusType;
  private List<CaseDetailMocker> recordResult;
  private boolean desensitized;
}
