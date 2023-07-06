package com.arextest.web.core.business.util;

import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SchemaUtils {
    public static void mergeMap(Map<String, Object> contract, Map<String, Object> model) {
        model.forEach((key, value) -> mergeEntry(contract, key, value));
    }

    public static String mergeJson(String contract, String model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> contractMap = contract == null ? new HashMap<>() : objectMapper.readValue(contract, Map.class);
            if (model == null) return contract;
            Map<String, Object> modelMap = objectMapper.readValue(model, Map.class);
            mergeMap(contractMap, modelMap);
            return objectMapper.writeValueAsString(contractMap);
        } catch (JsonProcessingException e) {
            LogUtils.error(LOGGER, "objectMapper readValue failed! contract:{}, model:{}", contract, model);
        }
        return null;
    }

    private static void mergeEntry(Map<String, Object> contract, String key, Object value) {
        if (value instanceof Map<?, ?>) {
            Map<String, Object> mapContract = (Map<String, Object>) contract.getOrDefault(key, new HashMap<>());
            mergeMap(mapContract, (Map<String, Object>) value);
            contract.put(key, mapContract);
        } else if (value instanceof List) {
            List<Object> listContract = (List<Object>) contract.getOrDefault(key, new ArrayList<>());
            mergeList(listContract, (List<Object>) value);
            contract.put(key, listContract);
        } else {
            if (contract.get(key) == null) {
                contract.put(key, handlePrimaryItem(value));
            }
        }
    }

    private static void mergeList(List<Object> contract, List<Object> model) {
        Object contractItem = null;
        if (!contract.isEmpty()) {
            contractItem = contract.remove(0);
        }

        for (Object item : model) {
            if (item instanceof Map<?, ?>) {
                if (contractItem == null) {
                    contractItem = new HashMap<String, Object>();
                }
                mergeMap((Map<String, Object>) contractItem, (Map<String, Object>) item);
            } else if (item instanceof List) {
                if (contractItem == null) {
                    contractItem = new ArrayList<>();
                }
                mergeList((List<Object>) contractItem, (List<Object>) item);
            } else {
                if (contractItem == null) {
                    contractItem = handlePrimaryItem(item);
                }
            }
        }
        contract.add(contractItem);
    }

    private static Object handlePrimaryItem(Object item) {
        if (item instanceof Integer || item instanceof Long) {
            return 1;
        }
        if (item instanceof Double || item instanceof Float) {
            return 0.1;
        }
        if (item instanceof Boolean) {
            return Boolean.TRUE;
        }
        if (item instanceof String) {
            return "String";
        }
        if (item instanceof char[]) {
            if (((char[]) item).length == 1) {
                return 'c';
            } else {
                return "String";
            }
        }

        LogUtils.error(LOGGER, "Unsupported Type, item:{}, class:{}", item, item.getClass());
        return null;
    }
}
