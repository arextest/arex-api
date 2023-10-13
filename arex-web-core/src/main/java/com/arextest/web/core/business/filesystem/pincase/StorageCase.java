package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.model.replay.ViewRecordResponseType;
import com.arextest.web.common.HttpUtils;
import com.arextest.web.common.LogUtils;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
public class StorageCase {
    private static final String RECORD_ID = "recordId";
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String DASH = "-";

    private static final String STORAGE_VIEW_RECORD_URL = "/api/storage/replay/query/viewRecord";
    private static final String STORAGE_PIN_CASE_URL = "/api/storage/edit/pinned/";
    private static final String CONFIG_BATCH_NO = "configBatchNo";
    public static final String PINNED = "Pinned";
    public static final String SOURCE_PROVIDER = "sourceProvider";

    @Value("${arex.storage.service.url}")
    private String storageServiceUrl;

    @Resource
    private MockerConversionFactory factory;

    @Resource
    private ObjectMapper objectMapper;

    @SneakyThrows
    public FSCaseDto getViewRecord(String recordId) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put(RECORD_ID, recordId);
        ResponseEntity<ViewRecordResponseType>
                response =
                HttpUtils.post(storageServiceUrl + STORAGE_VIEW_RECORD_URL,
                        request.toString(),
                        ViewRecordResponseType.class);
        if (response == null || response.getBody() == null || response.getBody().getRecordResult() == null) {
            return null;
        }

        List<AREXMocker> mockers = response.getBody().getRecordResult();
        Optional<AREXMocker> entryPoint = mockers.stream()
                .filter(m -> (m.getCategoryType() != null && m.getCategoryType().isEntryPoint())).findFirst();
        String categoryName =
                entryPoint.map(AREXMocker::getCategoryType).map(MockCategoryType::getName).orElse(StringUtils.EMPTY);

        if (StringUtils.isBlank(categoryName) || factory.get(categoryName) == null) {
            return null;
        }
        MockerConversion mockerConversion = factory.get(categoryName);

        return mockerConversion.mockerConvertToFsCase(entryPoint.get());
    }


    public String getConfigBatchNo(String recordId) {
        AREXMocker arexMocker = getPinnedArexEntryPointMocker(recordId);
        return  Optional.ofNullable(arexMocker)
                .map(AREXMocker::getTargetRequest)
                .map(request -> request.getAttribute(CONFIG_BATCH_NO))
                .map(Object::toString)
                .orElse(null);
    }


    public AREXMocker getPinnedArexEntryPointMocker(String recordId) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put(RECORD_ID, recordId);
        request.put(SOURCE_PROVIDER, PINNED);
        ResponseEntity<ViewRecordResponseType>
            response =
            HttpUtils.post(storageServiceUrl + STORAGE_VIEW_RECORD_URL,
                request.toString(),
                ViewRecordResponseType.class);
        if (response == null || response.getBody() == null || response.getBody().getRecordResult() == null) {
            return null;
        }

        List<AREXMocker> mockers = response.getBody().getRecordResult();
        return mockers.stream().filter(m -> m.getCategoryType() != null && m.getCategoryType().isEntryPoint())
            .findFirst().orElse(null);
    }


    public String getNewRecordId(String recordId) {
        return recordId
                + DASH
                + System.currentTimeMillis()
                + DASH
                + new Random(System.currentTimeMillis()).nextInt(99);
    }

    public boolean pinnedCase(String recordId, String newRecordId) {
        try {
            String url = storageServiceUrl + STORAGE_PIN_CASE_URL + recordId + "/" + newRecordId + "/";
            ResponseEntity<CopyResponseType> response = HttpUtils.get(url, CopyResponseType.class);
            if (response == null || response.getBody() == null) {
                return false;
            }
            if (response.getBody().getCopied() == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "Failed to pinned case.", e);
            return false;
        }
    }

    @Data
    private static class CopyResponseType {
        private int copied;
    }
}