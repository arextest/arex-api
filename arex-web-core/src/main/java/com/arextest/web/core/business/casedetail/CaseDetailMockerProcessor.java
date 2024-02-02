package com.arextest.web.core.business.casedetail;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.mock.Mocker;
import com.arextest.model.mock.Mocker.Target;
import com.arextest.web.core.business.util.JsonUtils;
import com.arextest.web.model.contract.contracts.casedetail.CaseDetailMocker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * Created by Qzmo on 2023/12/6
 */
@Slf4j
public class CaseDetailMockerProcessor {

  // Arex agent encoded content field for http client mocker
  private static final String HTTP_CLIENT_ENCODED_FIELD = "content";
  public static List<CaseDetailMocker> convertMocker(List<AREXMocker> mockers) {
    List<CaseDetailMocker> results = new ArrayList<>(mockers.size());
    for (AREXMocker mocker : mockers) {
      CaseDetailMocker caseDetailMocker = new CaseDetailMocker();
      BeanUtils.copyProperties(mocker, caseDetailMocker);
      Target req = mocker.getTargetRequest();
      if (req != null) {
        CaseDetailMocker.CaseDetailTarget caseDetailReq = new CaseDetailMocker.CaseDetailTarget();
        BeanUtils.copyProperties(req, caseDetailReq);
        caseDetailReq.setBody(tryParse(mocker, req.getBody()));
        caseDetailMocker.setTargetRequest(caseDetailReq);
      }

      Target res = mocker.getTargetResponse();
      if (res != null) {
        CaseDetailMocker.CaseDetailTarget caseDetailRes = new CaseDetailMocker.CaseDetailTarget();
        BeanUtils.copyProperties(res, caseDetailRes);
        caseDetailRes.setBody(tryParse(mocker, res.getBody()));
        caseDetailMocker.setTargetResponse(caseDetailRes);
      }

      results.add(caseDetailMocker);
    }
    return results;
  }

  private static Object tryParse(AREXMocker mocker, String body) {
    // extra parsing for http client mocker
    if (MockCategoryType.HTTP_CLIENT.equals(mocker.getCategoryType())) {
      Object root = JsonUtils.tryParseBase64Json(body);

      // if root is json node, try to parse the encoded content node
      if (root instanceof JsonNode) {
        try {
          ObjectNode jsonRoot = (ObjectNode) root;
          JsonNode contentNode = jsonRoot.get(HTTP_CLIENT_ENCODED_FIELD);
          if (contentNode == null || contentNode.isNull()) {
            return root;
          }
          Object parsedContentNode = JsonUtils.tryParseBase64Json(contentNode.asText());
          if (parsedContentNode instanceof JsonNode) {
            jsonRoot.set(HTTP_CLIENT_ENCODED_FIELD, (JsonNode) parsedContentNode);
          }
          return jsonRoot;
        } catch (Exception e) {
          return root;
        }
      } else {
        return root;
      }
    } else {
      return JsonUtils.tryParseBase64Json(body);
    }
  }
}
