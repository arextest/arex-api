package com.arextest.web.core.business.filesystem;


import cn.hutool.json.JSONUtil;
import com.arextest.web.common.HttpUtils;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class StorageCase {
    private static final String RECORD_ID = "recordId";
    private static final String AND = "&";
    private static final String EQUAL = "=";

    private static final String STORAGE_VIEW_RECORD_URL = "/api/report/record/fixRecord";

    @Value("${arex.storage.service.url}")
    private String storageServiceUrl;

    @Resource
    private ObjectMapper objectMapper;

    @SneakyThrows
    public StorageCaseEntity getViewRecord(String recordId) {
        JSONObject request = new JSONObject();
        request.put(RECORD_ID, recordId);
        ResponseEntity<String>
                response =
                HttpUtils.post(storageServiceUrl + STORAGE_VIEW_RECORD_URL, request.toString(), String.class);
        if (response == null || StringUtils.isEmpty(response.getBody())) {
            return null;
        }
        StorageViewRecordEntity entity = JSONUtil.toBean(response.getBody(), StorageViewRecordEntity.class);
        if (entity.recordResult == null || !entity.recordResult.containsKey(15)) {
            return null;
        }
        List<String> mainMessages = entity.recordResult.get(15);
        if (mainMessages.isEmpty()) {
            return null;
        }

        return JSONUtil.toBean(mainMessages.get(0), StorageCaseEntity.class);
    }

    public FSCaseDto getCase(String parentId, String caseId, StorageCaseEntity entity) {
        if (entity == null) {
            return null;
        }
        FSCaseDto caseDto = new FSCaseDto();
        caseDto.setParentId(parentId);
        caseDto.setId(caseId);
        caseDto.setRecordId(entity.getRecordId());
        AddressDto addressDto = new AddressDto();
        addressDto.setMethod(entity.getMethod());
        addressDto.setEndpoint(entity.getPath());
        caseDto.setAddress(addressDto);

        List<KeyValuePairDto> kvPair = new ArrayList<>();
        if (StringUtils.isNotEmpty(entity.getRequestHeaders())) {
            try {
                Map<String, String> headers = objectMapper.readValue(entity.getRequestHeaders(), HashMap.class);
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    KeyValuePairDto kv = new KeyValuePairDto();
                    kv.setKey(header.getKey());
                    kv.setValue(header.getValue());
                    kv.setActive(true);
                    kvPair.add(kv);
                }
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to parse headers", e);
            }

        }
        caseDto.setHeaders(kvPair);
        if (!StringUtils.isEmpty(entity.getRequest())) {
            if (Objects.equals(entity.getMethod(), HttpMethod.GET.name())) {
                String[] params = entity.getRequest().split(AND);
                List<KeyValuePairDto> paramKvPair = new ArrayList<>();
                for (String param : params) {
                    String[] kv = param.split(EQUAL);
                    if (kv.length != 2) {
                        continue;
                    }
                    KeyValuePairDto paramKv = new KeyValuePairDto();
                    paramKv.setKey(kv[0]);
                    paramKv.setValue(kv[1]);
                    paramKv.setActive(true);
                    paramKvPair.add(paramKv);
                }
                caseDto.setParams(paramKvPair);
            } else {
                BodyDto bodyDto = new BodyDto();
                bodyDto.setBody(entity.getRequest());
                caseDto.setBody(bodyDto);
            }
        }
        return caseDto;
    }

    @Data
    public class StorageViewRecordEntity {
        public StorageViewRecordEntity() {
        }
        private Map<Integer, List<String>> recordResult;
    }


    @Data
    public class StorageCaseEntity {
        public StorageCaseEntity() {
        }

        private String recordId;
        private String request;
        private String method;
        private String path;
        private String requestHeaders;
    }
}
