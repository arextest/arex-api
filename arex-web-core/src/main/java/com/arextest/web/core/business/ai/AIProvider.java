package com.arextest.web.core.business.ai;

import com.arextest.web.model.contract.contracts.ai.FixReq;
import com.arextest.web.model.contract.contracts.ai.GenReq;
import com.arextest.web.model.contract.contracts.ai.ModelInfo;
import com.arextest.web.model.contract.contracts.ai.TestScriptFixRes;
import com.arextest.web.model.contract.contracts.ai.TestScriptGenRes;
import lombok.NonNull;

/**
 * @author: QizhengMo
 * @date: 2024/3/22 16:04
 */
public interface AIProvider {
  TestScriptGenRes generateScripts(GenReq genReq);
  TestScriptFixRes fixScript(FixReq fixReq);
  @NonNull ModelInfo getModelInfo();
}
