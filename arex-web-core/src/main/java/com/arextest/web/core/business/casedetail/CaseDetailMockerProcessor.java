package com.arextest.web.core.business.casedetail;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.Mocker;
import com.arextest.model.mock.Mocker.Target;
import com.arextest.web.core.business.util.JsonUtils;
import com.arextest.web.model.contract.contracts.casedetail.CaseDetailMocker;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * Created by Qzmo on 2023/12/6
 */
@Slf4j
public class CaseDetailMockerProcessor {
  public static List<CaseDetailMocker> convertMocker(List<AREXMocker> mockers) {
    List<CaseDetailMocker> results = new ArrayList<>(mockers.size());
    for (Mocker mocker : mockers) {
      CaseDetailMocker caseDetailMocker = new CaseDetailMocker();
      BeanUtils.copyProperties(mocker, caseDetailMocker);
      Target req = mocker.getTargetRequest();
      if (req != null) {
        CaseDetailMocker.CaseDetailTarget caseDetailReq = new CaseDetailMocker.CaseDetailTarget();
        BeanUtils.copyProperties(req, caseDetailReq);
        caseDetailReq.setBody(JsonUtils.tryParseJson(req.getBody()));
        caseDetailMocker.setTargetRequest(caseDetailReq);
      }

      Target res = mocker.getTargetResponse();
      if (res != null) {
        CaseDetailMocker.CaseDetailTarget caseDetailRes = new CaseDetailMocker.CaseDetailTarget();
        BeanUtils.copyProperties(res, caseDetailRes);
        caseDetailRes.setBody(JsonUtils.tryParseJson(res.getBody()));
        caseDetailMocker.setTargetResponse(caseDetailRes);
      }

      results.add(caseDetailMocker);
    }
    return results;
  }
}
