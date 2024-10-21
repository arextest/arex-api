package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.exception.RecordCaseNotFoundArexException;
import com.arextest.web.common.exception.UnsupportedCategoryArexException;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import com.arextest.web.model.contract.contracts.casedetail.ViewRecordResponseType;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StorageCase {

  public static final String PINNED = "Pinned";
  public static final String SOURCE_PROVIDER = "sourceProvider";
  private static final String RECORD_ID = "recordId";
  private static final String DASH = "-";
  private static final String STORAGE_VIEW_RECORD_URL = "/api/storage/replay/query/viewRecord";
  private static final String STORAGE_PIN_CASE_URL = "/api/storage/edit/pinned/";
  private static final String STORAGE_COPY_CASE_URL = "/api/storage/edit/copy/?srcProviderName=Pinned&targetProviderName=Pinned&srcRecordId=%s&targetRecordId=%s";
  private static final String CONFIG_BATCH_NO = "configBatchNo";
  private static final Random RANDOM = new Random();

  @Value("${arex.storage.service.url}")
  private String storageServiceUrl;

  @Resource
  private MockerConversionFactory factory;

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  @SneakyThrows
  public Pair<FSCaseDto, String> getViewRecord(String recordId) {
    ObjectNode request = objectMapper.createObjectNode();
    request.put(RECORD_ID, recordId);

    ViewRecordResponseType response = httpWebServiceApiClient.post(
        storageServiceUrl + STORAGE_VIEW_RECORD_URL, request.toString(),
        ViewRecordResponseType.class);

    Optional.ofNullable(response)
        .map(ViewRecordResponseType::getRecordResult)
        .filter(result -> !result.isEmpty())
        .orElseThrow(
            () -> new RecordCaseNotFoundArexException("Record case not found: " + recordId));

    List<AREXMocker> mockers = response.getRecordResult();
    Optional<AREXMocker> entryPoint = mockers.stream()
        .filter(m -> (m.getCategoryType() != null && m.getCategoryType().isEntryPoint()))
        .findFirst();
    String categoryName =
        entryPoint.map(AREXMocker::getCategoryType).map(MockCategoryType::getName)
            .orElse(StringUtils.EMPTY);

    if (StringUtils.isBlank(categoryName) || factory.get(categoryName) == null) {
      throw new UnsupportedCategoryArexException("Unsupported mock category: " + categoryName);
    }
    MockerConversion mockerConversion = factory.get(categoryName);

    FSCaseDto fsCaseDto = mockerConversion.mockerConvertToFsCase(entryPoint.get());
    String appId = entryPoint.map(AREXMocker::getAppId).orElse(StringUtils.EMPTY);
    return Pair.of(fsCaseDto, appId);
  }

  public String getConfigBatchNo(String recordId) {
    AREXMocker arexMocker = getPinnedArexEntryPointMocker(recordId);
    return Optional.ofNullable(arexMocker).map(AREXMocker::getTargetRequest)
        .map(request -> request.getAttribute(CONFIG_BATCH_NO)).map(Object::toString).orElse(null);
  }

  public AREXMocker getPinnedArexEntryPointMocker(String recordId) {
    ObjectNode request = objectMapper.createObjectNode();
    request.put(RECORD_ID, recordId);
    request.put(SOURCE_PROVIDER, PINNED);
    ViewRecordResponseType response = httpWebServiceApiClient.post(
        storageServiceUrl + STORAGE_VIEW_RECORD_URL, request.toString(),
        ViewRecordResponseType.class);

    if (response == null || response.getRecordResult() == null) {
      return null;
    }

    List<AREXMocker> mockers = response.getRecordResult();
    return mockers.stream()
        .filter(m -> m.getCategoryType() != null && m.getCategoryType().isEntryPoint())
        .findFirst().orElse(null);
  }

  public String getNewRecordId(String recordId) {
    // avoid extremely long recordId, limit to 30 characters
    String trimmed = recordId.length() > 30 ? recordId.substring(0, 30) : recordId;
    return trimmed + DASH + System.currentTimeMillis() + DASH + RANDOM.nextInt(99);
  }

  public boolean pinnedCase(String recordId, String newRecordId) {
    try {
      String url = storageServiceUrl + STORAGE_PIN_CASE_URL + recordId + "/" + newRecordId + "/";
      CopyResponseType response = httpWebServiceApiClient.get(url,
          Collections.emptyMap(),
          CopyResponseType.class);
      if (response == null || response.getCopied() == 0) {
        return false;
      }
      return true;
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to pinned case.", e);
      return false;
    }
  }

  public String copyPinnedCase(String oldId) {
    String newId = getNewRecordId(oldId);
    try {
      String url = storageServiceUrl + String.format(STORAGE_COPY_CASE_URL, oldId, newId);
      CopyResponseType response = httpWebServiceApiClient.get(url,
          Collections.emptyMap(),
          CopyResponseType.class);
      if (response == null || response.getCopied() == 0) {
        return null;
      }
      return newId;
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to copy case.", e);
      return null;
    }
  }

  @Data
  private static class CopyResponseType {

    private int copied;
  }
}