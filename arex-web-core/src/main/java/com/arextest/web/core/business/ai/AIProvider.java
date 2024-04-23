package com.arextest.web.core.business.ai;

import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import com.arextest.web.model.dto.vertexai.TestScriptGenRes;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */
public interface AIProvider {
  TestScriptGenRes generateScripts(GenReq genReq);
}
