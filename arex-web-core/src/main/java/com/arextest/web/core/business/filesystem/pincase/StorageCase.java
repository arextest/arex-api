package com.arextest.web.core.business.filesystem.pincase;

import com.arextest.common.model.response.GenericResponseType;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.exception.RecordCaseNotFoundArexException;
import com.arextest.web.common.exception.UnsupportedCategoryArexException;
import com.arextest.web.core.business.beans.httpclient.HttpWebServiceApiClient;
import com.arextest.web.core.business.util.JsonUtils;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.model.contract.contracts.casedetail.ViewRecordResponseType;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Resource;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StorageCase {

  public static final String PINNED = "Pinned";
  public static final String SOURCE_PROVIDER = "sourceProvider";
  private static final String RECORD_ID = "recordId";
  private static final String DASH = "-";
  private static final String CONFIG_BATCH_NO = "configBatchNo";
  private static final String AREX_RECORD_ID = "arex-record-id";

  private static final String LOCAL_HOST = "http://127.0.0.1";
  private static final String DEFAULT_PORT = "8080";
  private static final String HOST_KEY = "host";
  private static final String COLON = ":";
  private static final String NULL_PLAN_ID = "undefined";
  private static final String SKIP_MOCK_HEADER = "X-AREX-Exclusion-Operations";
  private static final String AREX_REPLAY_PREPARE_DEPENDENCY = "arex_replay_prepare_dependency";


  private static final String STORAGE_VIEW_RECORD_URL = "/api/storage/replay/query/viewRecord";
  private static final String STORAGE_PIN_CASE_URL = "/api/storage/edit/pinned/";
  private static final String STORAGE_COPY_CASE_URL = "/api/storage/edit/copy/?srcProviderName=Pinned&targetProviderName=Pinned&srcRecordId=%s&targetRecordId=%s";
  private static final String STORAGE_PIN_BATCHADD_URL = "/api/storage/edit/pinned/batchAdd/";

  private static final Random RANDOM = new Random();

  @Value("${arex.storage.service.url}")
  private String storageServiceUrl;

  @Resource
  private MockerConversionFactory factory;

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private HttpWebServiceApiClient httpWebServiceApiClient;

  @Resource
  private ConfigRepositoryProvider<ScheduleConfiguration> scheduleConfigurationProvider;

  @Resource
  private ReportPlanStatisticRepository reportPlanStatisticRepository;


  @SneakyThrows
  public FSCaseDto getViewRecord(String recordId, String planId) throws RuntimeException {
    ObjectNode request = objectMapper.createObjectNode();
    request.put(RECORD_ID, recordId);

    ViewRecordResponseType response = httpWebServiceApiClient.post(
        storageServiceUrl + STORAGE_VIEW_RECORD_URL, request.toString(),
        ViewRecordResponseType.class);

    Optional.ofNullable(response).map(ViewRecordResponseType::getRecordResult)
        .filter(result -> !result.isEmpty()).orElseThrow(
            () -> new RecordCaseNotFoundArexException("Record case not found: " + recordId));

    List<AREXMocker> mockers = response.getRecordResult();
    return convertMockerToFsCase(mockers, recordId, planId, true);
  }


  public FSCaseDto convertMockerToFsCase(List<AREXMocker> mockers, String recordId, String planId,
      boolean withSkipMocker) {

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

    FSCaseDto caseDto = mockerConversion.mockerConvertToFsCase(entryPoint.get());

    if (caseDto == null) {
      return null;
    }

    if (caseDto.getHeaders() == null) {
      caseDto.setHeaders(new ArrayList<>());
    }

    if (StringUtils.isNotEmpty(recordId)) {
      KeyValuePairDto header = new KeyValuePairDto();
      header.setKey(AREX_RECORD_ID);
      header.setValue(recordId);
      header.setActive(true);
      caseDto.getHeaders().add(0, header);

      if (withSkipMocker) {
        String appId = entryPoint.map(AREXMocker::getAppId).orElse(StringUtils.EMPTY);
        KeyValuePairDto skipMockHeader = generateSkipMockHeader(appId);
        if (skipMockHeader != null) {
          caseDto.getHeaders().add(skipMockHeader);
        }
      }

      KeyValuePairDto configFileHeader = generateConfigFileHeader(mockers);
      if (configFileHeader != null) {
        caseDto.getHeaders().add(configFileHeader);
      }
    }

    if (StringUtils.equalsIgnoreCase(planId, NULL_PLAN_ID)) {
      String oldHost = caseDto.getHeaders().stream()
          .filter(h -> h.getKey().equalsIgnoreCase(HOST_KEY))
          .findFirst()
          .map(KeyValuePairDto::getValue)
          .orElse(null);
      String port = oldHost == null || !oldHost.contains(COLON)
          ? DEFAULT_PORT : oldHost.split(COLON)[1];
      caseDto.getAddress().setEndpoint(
          contactUrl(LOCAL_HOST + COLON + port, caseDto.getAddress().getEndpoint())
      );
    } else {
      setAddressEndpoint(planId, caseDto.getAddress());
    }
    return caseDto;
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
        .filter(m -> m.getCategoryType() != null && m.getCategoryType().isEntryPoint()).findFirst()
        .orElse(null);
  }

  public String getNewRecordId(String recordId) {
    // avoid extremely long recordId, limit to 30 characters
    String trimmed = recordId.length() > 30 ? recordId.substring(0, 30) : recordId;
    return trimmed + DASH + System.currentTimeMillis() + DASH + RANDOM.nextInt(99);
  }

  public boolean pinnedCase(String recordId, String newRecordId) {
    try {
      String url = storageServiceUrl + STORAGE_PIN_CASE_URL + recordId + "/" + newRecordId + "/";
      CopyResponseType response = httpWebServiceApiClient.get(url, Collections.emptyMap(),
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

  public boolean batchPinnedCase(String recordId, List<AREXMocker> arexMockers) {
    if (CollectionUtils.isEmpty(arexMockers)) {
      return true;
    }
    try {
      arexMockers.forEach(item -> {
        item.setRecordId(recordId);
      });
      String url = storageServiceUrl + STORAGE_PIN_BATCHADD_URL;
      GenericResponseType response = httpWebServiceApiClient.post(url, arexMockers,
          GenericResponseType.class);
      return response != null && response.getBody() != null && Boolean.parseBoolean(
          response.getBody().toString());
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to pinned case.", e);
      return false;
    }
  }

  private KeyValuePairDto generateSkipMockHeader(String appId) {
    List<ScheduleConfiguration> scheduleConfigurations = scheduleConfigurationProvider.listBy(
        appId);
    if (CollectionUtils.isEmpty(scheduleConfigurations)) {
      return null;
    }
    ScheduleConfiguration scheduleConfiguration = scheduleConfigurations.get(0);
    if (MapUtils.isEmpty(scheduleConfiguration.getExcludeOperationMap())) {
      return null;
    }
    String kvValue = JsonUtils.toJsonString(scheduleConfiguration.getExcludeOperationMap());
    if (StringUtils.isBlank(kvValue)) {
      return null;
    }
    KeyValuePairDto kvDto = new KeyValuePairDto();
    kvDto.setKey(SKIP_MOCK_HEADER);
    kvDto.setValue(kvValue);
    kvDto.setActive(true);
    return kvDto;
  }

  private KeyValuePairDto generateConfigFileHeader(List<AREXMocker> mockers) {
    String configBatchNo = Optional.ofNullable(mockers)
        .orElse(Collections.emptyList())
        .stream()
        .filter(mocker -> mocker != null && Objects.equals(mocker.getCategoryType(),
            MockCategoryType.CONFIG_FILE))
        .map(AREXMocker::getTargetRequest)
        .filter(Objects::nonNull)
        .map(targetRequest -> targetRequest.getAttribute(CONFIG_BATCH_NO))
        .map(Object::toString)
        .findFirst()
        .orElse(null);

    if (StringUtils.isBlank(configBatchNo)) {
      return null;
    }
    KeyValuePairDto kvDto = new KeyValuePairDto();
    kvDto.setKey(AREX_REPLAY_PREPARE_DEPENDENCY);
    kvDto.setValue(configBatchNo);
    kvDto.setActive(true);
    return kvDto;


  }

  private void setAddressEndpoint(String planId, AddressDto addressDto) {
    ReportPlanStatisticDto reportPlanStatisticDto = reportPlanStatisticRepository.findByPlanId(
        planId);
    if (addressDto != null) {
      addressDto.setEndpoint(this.contactUrl(
          reportPlanStatisticDto == null ? StringUtils.EMPTY
              : reportPlanStatisticDto.getTargetEnv(),
          addressDto.getEndpoint()));
    }
  }

  private String contactUrl(String domain, String operation) {
    String result;
    domain = Optional.ofNullable(domain).orElse(StringUtils.EMPTY);
    operation = Optional.ofNullable(operation).orElse(StringUtils.EMPTY);
    boolean domainContain = StringUtils.endsWith(domain, "/");
    boolean operationContain = StringUtils.startsWith(operation, "/");
    if (domainContain && operationContain) {
      result = domain + operation.substring(1);
    } else if (!domainContain && !operationContain) {
      result = domain + "/" + operation;
    } else {
      result = domain + operation;
    }
    return result;
  }


  @Data
  private static class CopyResponseType {

    private int copied;
  }
}