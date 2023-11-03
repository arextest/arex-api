package com.arextest.web.core.business.util;

import com.arextest.common.utils.JsonTraverseUtils;
import com.arextest.web.model.dto.CompareResultDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wildeslam.
 * @create 2023/10/13 17:33
 */
@Slf4j
public class JsonUtils {

  public static void downgrade(CompareResultDto compareResult) {
    try {
      if (isJsonStr(compareResult.getBaseMsg())) {
        compareResult.setBaseMsg(JsonTraverseUtils.trimAllLeaves(compareResult.getBaseMsg()));
      } else {
        compareResult.setBaseMsg(null);
      }
      if (isJsonStr(compareResult.getTestMsg())) {
        compareResult.setTestMsg(JsonTraverseUtils.trimAllLeaves(compareResult.getTestMsg()));
      } else {
        compareResult.setTestMsg(null);
      }
    } catch (Exception e) {
      LOGGER.error("trimAllLeaves error", e);
    }

  }

  public static boolean isJsonStr(String obj) {
    return StringUtils.isNotEmpty(obj) && obj.startsWith("{") && obj.endsWith("}");
  }
}
