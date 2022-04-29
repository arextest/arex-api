package com.arextest.report.core.business.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arextest.report.common.HttpUtils;
import org.springframework.http.ResponseEntity;

import java.util.List;


public class ConfigServiceUtils {

    private static final String BODY = "body";

    public static Object sendGetHttpRequest(String url) {
        
        ResponseEntity<String> responseEntity = HttpUtils.get(url, String.class);
        Object bodyObj = null;
        JSONObject entityBody = new JSONObject(responseEntity.getBody());
        bodyObj = entityBody.get(BODY);
        return bodyObj;
    }

    public static boolean sendPostHttpRequest(String url, Object request) {
        ResponseEntity<String> responseEntity = HttpUtils.post(url, JSONUtil.toJsonStr(request), String.class);
        Object bodyObj = null;
        JSONObject entityBody = new JSONObject(responseEntity.getBody());
        bodyObj = entityBody.get(BODY);
        return Boolean.TRUE.equals(bodyObj);
    }


    public static  <T> T produceEntity(Object obj, Class entityClass) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONObject) {
            return (T) JSONUtil.toBean((JSONObject) obj, entityClass);
        }
        return null;
    }

    public static List produceListEntity(Object obj, Class entityClass) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONArray) {
            return JSONUtil.toList((JSONArray) obj, entityClass);
        }
        return null;
    }

}
