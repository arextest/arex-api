package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.util.SchemaUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.OverwriteContractRequestType;
import com.arextest.web.model.contract.contracts.QueryContractRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgSchemaRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgSchemaResponseType;
import com.arextest.web.model.contract.contracts.QuerySchemaForConfigRequestType;
import com.arextest.web.model.contract.contracts.SyncResponseContractRequestType;
import com.arextest.web.model.contract.contracts.SyncResponseContractResponseType;
import com.arextest.web.model.contract.contracts.common.DependencyWithContract;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
public class SchemaInferService {

    @Resource
    private ReplayCompareResultRepository replayCompareResultRepository;

    @Resource
    private AppContractRepository appContractRepository;

    @Resource
    private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    private static final String TYPE = "type";
    private static final String PROPERTIES = "properties";
    private static final String ITEMS = "items";
    private static final String OBJECT = "object";
    private static final String ARRAY = "array";
    private static final String NULL_STR = "null";

    private static final String ANY_OF = "anyOf";

    private static final String VALUE_WITH_SYMBOL = "%value%";

    private static final String EMPTY_CONTRACT = "{}";

    private static final int LIMIT = 5;

    private static final int FIRST_INDEX = 0;

    private static final ObjectMapper CONTRACT_OBJ_MAPPER = new ObjectMapper();

    private static final JsonSchemaInferrer INFERRER = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_06)
            .build();


    public QueryMsgSchemaResponseType schemaInfer(QueryMsgSchemaRequestType request) {
        QueryMsgSchemaResponseType response = new QueryMsgSchemaResponseType();
        String msg;
        if (request.getId() != null) {
            CompareResultDto dto = replayCompareResultRepository.queryCompareResultsById(request.getId());
            msg = request.isUseTestMsg() ? dto.getTestMsg() : dto.getBaseMsg();
        } else {
            msg = request.getMsg();
        }
        if (StringUtils.isEmpty(msg)) {
            return response;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(msg);
            JsonNode jsonSchema = INFERRER.inferForSample(jsonNode);
            adjustJsonNode(jsonSchema, false);
            JsonNode schemaByPath = getSchemaByPath(jsonSchema, request.getListPath());
            if (schemaByPath != null) {
                response.setSchema(mapper.writeValueAsString(schemaByPath));
            }
        } catch (Exception e) {
            LogUtils.warn(LOGGER, "schemaInfer", e);
        }
        return response;
    }

    public QueryMsgSchemaResponseType schemaInferForConfig(QuerySchemaForConfigRequestType request) {
        QueryMsgSchemaResponseType response = new QueryMsgSchemaResponseType();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(request.getMsg());
            JsonNode jsonSchema = INFERRER.inferForSample(jsonNode);
            adjustJsonNode(jsonSchema, false);
            if (request.isOnlyArray()) {
                getArray(jsonSchema);
            }
            response.setSchema(mapper.writeValueAsString(jsonSchema));
        } catch (Exception e) {
            LogUtils.warn(LOGGER, "schemaInferForConfig", e);
        }
        return response;
    }

    public AppContractDto queryContract(QueryContractRequestType requestType) {
        if (requestType.getContractId() != null) {
            return appContractRepository.queryById(requestType.getContractId());
        } else if (requestType.getOperationId() != null) {
            return appContractRepository.queryAppContractByType(requestType.getOperationId(),
                    ContractTypeEnum.ENTRY.getCode());
        } else if (requestType.getAppId() != null) {
            return appContractRepository.queryAppContractByType(requestType.getAppId(),
                    ContractTypeEnum.GLOBAL.getCode());
        }
        return null;
    }

    public boolean overwriteContract(OverwriteContractRequestType request) {
        AppContractDto appContractDto = new AppContractDto();
        appContractDto.setContract(SchemaUtils.mergeJson(EMPTY_CONTRACT, request.getOperationResponse()));
        appContractDto.setAppId(request.getAppId());
        if (request.getContractId() != null) {
            appContractDto.setId(request.getContractId());
            appContractDto.setContractType(ContractTypeEnum.DEPENDENCY.getCode());
        } else if (request.getOperationId() != null) {
            appContractDto.setOperationId(request.getOperationId());
            appContractDto.setOperationName(request.getOperationName());
            appContractDto.setOperationType(request.getOperationType());
            appContractDto.setContractType(ContractTypeEnum.ENTRY.getCode());
        } else if (request.getAppId() != null) {
            appContractDto.setContractType(ContractTypeEnum.GLOBAL.getCode());
        }
        return appContractRepository.upsert(appContractDto);
    }

    public SyncResponseContractResponseType syncResponseContract(SyncResponseContractRequestType request) {
        String operationId = request.getOperationId();
        SyncResponseContractResponseType responseType = new SyncResponseContractResponseType();
        ApplicationOperationConfiguration applicationOperationConfiguration =
                applicationOperationConfigurationRepository.listByOperationId(operationId);
        Set<String> entryPointTypes = applicationOperationConfiguration.getOperationTypes();
        List<CompareResultDto> latestNEntryCompareResults =
                replayCompareResultRepository.queryLatestEntryPointCompareResult(operationId, entryPointTypes, LIMIT);
        if (CollectionUtils.isEmpty(latestNEntryCompareResults)) {
            return responseType;
        }
        CompareResultDto latestEntryCompareResult = latestNEntryCompareResults.get(FIRST_INDEX);
        List<String> planItemIds =
                latestNEntryCompareResults.stream().map(CompareResultDto::getPlanItemId).collect(Collectors.toList());
        List<String> recordIds =
                latestNEntryCompareResults.stream().map(CompareResultDto::getRecordId).collect(Collectors.toList());
        List<CompareResultDto> compareResultDtoList = replayCompareResultRepository.queryCompareResults(planItemIds,
                recordIds);

        // distinct by operationType-operationName
        Map<Pair<String, String>, List<CompareResultDto>> dependencyMap = new HashMap<>();
        for (CompareResultDto item : compareResultDtoList) {
            // filter dependency
            if (entryPointTypes.contains(item.getCategoryName())) {
                continue;
            }
            Pair<String, String> pair = new ImmutablePair<>(item.getCategoryName(), item.getOperationName());
            List<CompareResultDto> compareResultDtos = dependencyMap.getOrDefault(pair, new ArrayList<>());
            if (compareResultDtos.size() >= LIMIT) {
                continue;
            }
            compareResultDtos.add(item);
            dependencyMap.put(pair, compareResultDtos);
        }

        // entryPoint contract
        List<AppContractDto> upserts = new ArrayList<>();
        AppContractDto entryPointApplication = new AppContractDto();
        entryPointApplication.setOperationId(operationId);
        entryPointApplication.setOperationName(latestEntryCompareResult.getOperationName());
        entryPointApplication.setOperationType(latestEntryCompareResult.getCategoryName());
        entryPointApplication.setContract(perceiveContract(latestNEntryCompareResults));
        entryPointApplication.setAppId(applicationOperationConfiguration.getAppId());
        entryPointApplication.setContractType(ContractTypeEnum.ENTRY.getCode());
        upserts.add(entryPointApplication);

        dependencyMap.values().forEach(list -> {
            CompareResultDto compareResultDto = list.get(FIRST_INDEX);
            if (CollectionUtils.isNotEmpty(list)) {
                AppContractDto dependencyApplication = new AppContractDto();
                dependencyApplication.setContract(perceiveContract(list));
                dependencyApplication.setOperationId(operationId);
                dependencyApplication.setOperationName(compareResultDto.getOperationName());
                dependencyApplication.setOperationType(compareResultDto.getCategoryName());
                dependencyApplication.setAppId(applicationOperationConfiguration.getAppId());
                dependencyApplication.setContractType(ContractTypeEnum.DEPENDENCY.getCode());
                upserts.add(dependencyApplication);
            }
        });


        List<AppContractDto> appContractDtoList = appContractRepository.queryAppContractListByOpId(operationId);
        // pair of <type,name>, entryPoint doesn't need type to identify
        Map<Pair<String, String>, AppContractDto> existedMap = appContractDtoList.stream().collect(Collectors.toMap(
                item -> Objects.equals(item.getContractType(), ContractTypeEnum.ENTRY.getCode())
                        ? new ImmutablePair<>(null, item.getOperationName())
                        : new ImmutablePair<>(item.getOperationType(), item.getOperationName()),
                Function.identity()));
        // separate updates and inserts
        List<AppContractDto> updates = new ArrayList<>();
        List<AppContractDto> inserts = new ArrayList<>();
        Long currentTimeMillis = System.currentTimeMillis();
        for (AppContractDto item : upserts) {
            Pair<String, String> pair = Objects.equals(item.getContractType(), ContractTypeEnum.ENTRY.getCode())
                    ? new ImmutablePair<>(null, item.getOperationName())
                    : new ImmutablePair<>(item.getOperationType(), item.getOperationName());
            if (existedMap.containsKey(pair)) {
                String oldContract = existedMap.get(pair).getContract();
                // expand old contract but not overwrite simply
                if (!StringUtils.equals(oldContract, item.getContract())
                        && oldContract != null && !oldContract.equals(NULL_STR)) {
                    String newContract = SchemaUtils.mergeJson(oldContract, item.getContract());
                    item.setContract(newContract);
                }
                item.setId(existedMap.get(pair).getId());
                item.setDataChangeUpdateTime(currentTimeMillis);
                updates.add(item);
            } else {
                item.setDataChangeUpdateTime(currentTimeMillis);
                item.setDataChangeCreateTime(currentTimeMillis);
                inserts.add(item);
            }
        }
        if (CollectionUtils.isNotEmpty(updates)) {
            appContractRepository.update(updates);
        }
        if (CollectionUtils.isNotEmpty(inserts)) {
            List<AppContractDto> insertResults = new ArrayList<>(appContractRepository.insert(inserts));
            updates.addAll(insertResults);
        }

        List<DependencyWithContract> dependencyList = updates
                .stream()
                .filter(appContractDto -> !Objects.equals(appContractDto.getContractType(),
                        ContractTypeEnum.ENTRY.getCode()))
                .map(this::buildDependency)
                .collect(Collectors.toList());
        responseType.setEntryPointContractStr(entryPointApplication.getContract());
        responseType.setDependencyList(dependencyList);
        return responseType;
    }


    private DependencyWithContract buildDependency(AppContractDto appContractDto) {
        DependencyWithContract dependency = new DependencyWithContract();
        dependency.setDependencyId(appContractDto.getId());
        dependency.setDependencyName(appContractDto.getOperationName());
        dependency.setDependencyType(appContractDto.getOperationType());
        dependency.setContract(appContractDto.getContract());
        return dependency;
    }

    private String perceiveContract(List<CompareResultDto> compareResultDtoList) {
        Map<String, Object> contract = new HashMap<>();
        try {
            for (CompareResultDto compareResultDto : compareResultDtoList) {

                if (compareResultDto.getBaseMsg() != null) {
                    try {
                        SchemaUtils.mergeMap(contract, CONTRACT_OBJ_MAPPER.readValue(compareResultDto.getBaseMsg(),
                                Map.class));
                    } catch (JsonProcessingException e) {
                        LogUtils.error(LOGGER, "ObjectMapper readValue failed, exception:{}, msg:{}", e,
                                compareResultDto.getBaseMsg());
                    }
                }
                if (compareResultDto.getTestMsg() != null) {
                    try {
                        SchemaUtils.mergeMap(contract, CONTRACT_OBJ_MAPPER.readValue(compareResultDto.getTestMsg(),
                                Map.class));
                    } catch (JsonProcessingException e) {
                        LogUtils.error(LOGGER, "ObjectMapper readValue failed, exception:{}, msg:{}", e,
                                compareResultDto.getTestMsg());
                    }
                }
            }
            return CONTRACT_OBJ_MAPPER.writeValueAsString(contract);
        } catch (JsonProcessingException e2) {
            LogUtils.error(LOGGER, "ObjectMapper writeValue failed, exception:{}, contract:{}", e2, contract);
            return null;
        }
    }


    private void adjustJsonNode(JsonNode node, boolean isArray) {
        JsonNode typeNode = node.get(TYPE);
        if (typeNode == null) {
            return;
        }
        if (typeNode.isValueNode()) {
            String type = typeNode.asText();
            ObjectNode subNode;
            if (Objects.equals(type, OBJECT)) {
                subNode = (ObjectNode) node.get(PROPERTIES);
                if (subNode != null) {
                    Iterator<Map.Entry<String, JsonNode>> it = subNode.fields();
                    while (it.hasNext()) {
                        Map.Entry<String, JsonNode> entry = it.next();
                        adjustJsonNode(entry.getValue(), false);
                    }
                }
            } else if (Objects.equals(type, ARRAY)) {
                subNode = (ObjectNode) node.get(ITEMS);
                if (subNode != null) {
                    JsonNode anyOf = subNode.get(ANY_OF);
                    if (anyOf != null) {
                        subNode.remove(ANY_OF);
                        ((ObjectNode) node).set(ITEMS, removeNullNode(anyOf));
                    }
                    adjustJsonNode(node.get(ITEMS), true);
                }
            } else {
                if (isArray && !Objects.equals(type, NULL_STR)) {
                    JsonNode oldTypeNode = node.get(TYPE);
                    ObjectNode newNode = JsonNodeFactory.instance.objectNode();
                    ObjectNode newTypeNode = JsonNodeFactory.instance.objectNode();
                    newTypeNode.set(TYPE, JsonNodeFactory.instance.textNode(oldTypeNode.asText()));
                    newNode.set(VALUE_WITH_SYMBOL, newTypeNode);
                    ((ObjectNode) node).set(TYPE, JsonNodeFactory.instance.textNode(OBJECT));
                    ((ObjectNode) node).set(PROPERTIES, newNode);
                }
            }
        } else {
            ((ObjectNode) node).set(TYPE, removeNullType(typeNode));
            if (isArray) {
                JsonNode oldTypeNode = node.get(TYPE);
                ObjectNode newNode = JsonNodeFactory.instance.objectNode();
                ObjectNode newTypeNode = JsonNodeFactory.instance.objectNode();
                newTypeNode.set(TYPE, JsonNodeFactory.instance.textNode(oldTypeNode.asText()));
                newNode.set(VALUE_WITH_SYMBOL, newTypeNode);
                ((ObjectNode) node).set(TYPE, JsonNodeFactory.instance.textNode(OBJECT));
                ((ObjectNode) node).set(PROPERTIES, newNode);
            }
        }

    }

    private JsonNode removeNullNode(JsonNode node) {
        ArrayNode arrayNode = (ArrayNode) node;
        int size = arrayNode.size();
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(arrayNode.get(i).get(TYPE).asText(), NULL_STR)) {
                return arrayNode.get(i);
            }
        }
        return null;
    }

    private JsonNode removeNullType(JsonNode node) {
        ArrayNode arrayNode = (ArrayNode) node;
        int size = arrayNode.size();
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(arrayNode.get(i).asText(), NULL_STR)) {
                return arrayNode.get(i);
            }
        }
        return null;
    }


    private JsonNode getSchemaByPath(JsonNode jsonNode, String listPath) {
        if (jsonNode == null) {
            return null;
        }
        String[] split = listPath.split("\\\\");
        if (split.length == 1 && StringUtils.isEmpty(split[0]) && jsonNode.get(ITEMS) != null) {
            return eliminateNestedList(jsonNode);
        } else {
            for (String path : split) {
                if (jsonNode == null) {
                    return null;
                }
                JsonNode jsonSchema = eliminateNestedList(jsonNode);
                jsonNode = jsonSchema.get(PROPERTIES) != null ? jsonSchema.get(PROPERTIES).get(path) : null;
            }
        }
        return jsonNode == null ? null : jsonNode.get(ITEMS);
    }

    private JsonNode eliminateNestedList(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        String type = jsonNode.get(TYPE).asText();
        if (Objects.equals(type, ARRAY)) {
            return eliminateNestedList(jsonNode.get(ITEMS));
        } else {
            return jsonNode;
        }
    }

    private boolean getArray(JsonNode node) {
        JsonNode typeNode = node.get(TYPE);
        if (typeNode == null) {
            return false;
        }
        String type = typeNode.asText();
        ObjectNode subNode;
        if (Objects.equals(type, OBJECT)) {
            subNode = (ObjectNode) node.get(PROPERTIES);
            if (subNode != null) {
                List<String> names = Lists.newArrayList(subNode.fieldNames());
                for (String name : names) {
                    boolean isArray = getArray(subNode.get(name));
                    if (!isArray) {
                        subNode.remove(name);
                    }
                }
            } else {
                return false;
            }
        } else if (Objects.equals(type, ARRAY)) {
            subNode = (ObjectNode) node.get(ITEMS);
            if (subNode != null) {
                getArray(subNode);
            }
            return true;
        } else {
            return false;
        }
        return false;
    }
}
