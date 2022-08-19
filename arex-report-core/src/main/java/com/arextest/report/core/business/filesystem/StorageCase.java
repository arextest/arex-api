package com.arextest.report.core.business.filesystem;


import cn.hutool.json.JSONUtil;
import com.arextest.common.utils.CompressionUtils;
import com.arextest.report.common.HttpUtils;
import com.arextest.report.model.dto.KeyValuePairDto;
import com.arextest.report.model.dto.filesystem.AddressDto;
import com.arextest.report.model.dto.filesystem.BodyDto;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StorageCase {
    private static final String RECORD_ID = "recordId";

    private static final String STORAGE_VIEW_RECORD_URL = "/api/storage/replay/query/viewRecord";

    @Value("${arex.storage.service.url}")
    private String storageServiceUrl;

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
        String mainMessage = CompressionUtils.useZstdDecompress(mainMessages.get(0));

        return JSONUtil.toBean(mainMessage, StorageCaseEntity.class);
    }

    public FSCaseDto getCase(String parentId, String caseId, StorageCaseEntity entity) {
        if (entity == null) {
            return null;
        }
        FSCaseDto caseDto = new FSCaseDto();
        caseDto.setParentId(parentId);
        caseDto.setId(caseId);
        AddressDto addressDto = new AddressDto();
        addressDto.setMethod(entity.getMethod());
        addressDto.setEndpoint(entity.getPath());
        caseDto.setAddress(addressDto);

        List<KeyValuePairDto> kvPair = new ArrayList<>();
        if (entity.getRequestHeaders() != null) {
            for (Map.Entry<String, String> header : entity.getRequestHeaders().entrySet()) {
                KeyValuePairDto kv = new KeyValuePairDto();
                kv.setKey(header.getKey());
                kv.setValue(header.getValue());
                kv.setActive(true);
                kvPair.add(kv);
            }
        }
        caseDto.setHeaders(kvPair);
        if (!StringUtils.isEmpty(entity.getRequest())) {
            BodyDto bodyDto = new BodyDto();
            bodyDto.setBody(new String(Base64.getDecoder().decode(entity.getRequest())));
            caseDto.setBody(bodyDto);
        }
        return caseDto;
    }

    @Data
    public class StorageViewRecordEntity {
        private Map<Integer, List<String>> recordResult;
    }


    @Data
    public class StorageCaseEntity {
        private String recordId;
        private String request;
        private String method;
        private String path;
        private Map<String, String> requestHeaders;
    }
}
