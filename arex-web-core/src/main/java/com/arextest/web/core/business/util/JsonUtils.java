package com.arextest.web.core.business.util;

import com.arextest.common.utils.JsonTraverseUtils;
import com.arextest.web.model.dto.CompareResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wildeslam.
 * @create 2023/10/13 17:33
 */
@Slf4j
public class JsonUtils {

  public static final ObjectMapper COMMON_MAPPER = new ObjectMapper();

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

  public static Object tryParseJson(String jsonStr) {
    try {
      if (!isJsonStr(jsonStr)) {
        return jsonStr;
      }
      return COMMON_MAPPER.readTree(jsonStr);
    } catch (Exception e) {
      LOGGER.error("tryParseJson error", e);
      return jsonStr;
    }
  }

  public static Object tryParseBase64Json(String base64Str) {
    try {
      if (StringUtils.isBlank(base64Str)) {
        return base64Str;
      }
      if (isJsonStr(base64Str)) {
        return tryParseJson(base64Str);
      }

      // try decode and parse
      byte[] bytes = Base64.getDecoder().decode(base64Str.replace("\"", ""));
      return COMMON_MAPPER.readTree(bytes);
    } catch (Exception e) {
      // failed quite often, so don't log it
      return base64Str;
    }
  }

  public static boolean isJsonStr(String obj) {
    if (StringUtils.isBlank(obj)) {
      return false;
    }
    return (obj.startsWith("{") && obj.endsWith("}")) || (obj.startsWith("[") && obj.endsWith("]"));
  }

  public static String toJsonString(Object obj) {
    try {
      return COMMON_MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      LOGGER.error("toJsonString error", e);
      return null;
    }
  }
}
