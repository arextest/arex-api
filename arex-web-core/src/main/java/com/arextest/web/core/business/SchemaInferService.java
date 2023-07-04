package com.arextest.web.core.business;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.QueryMsgSchemaRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgSchemaResponseType;
import com.arextest.web.model.contract.contracts.QuerySchemaForConfigRequestType;
import com.arextest.web.model.contract.contracts.SyncResponseContractRequestType;
import com.arextest.web.model.contract.contracts.SyncResponseContractResponseType;
import com.arextest.web.model.contract.contracts.common.Dependency;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.dto.CompareResultDto;
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
import java.io.IOException;
import java.util.ArrayList;
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

    private static final int LIMIT = 5;

    private static final int FIRST_INDEX = 0;

    private static final JsonSchemaInferrer INFERRER = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_06)
            .build();


    public QueryMsgSchemaResponseType schemaInfer(QueryMsgSchemaRequestType request) {
        QueryMsgSchemaResponseType response = new QueryMsgSchemaResponseType();
        String msg = null;
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

    public SyncResponseContractResponseType syncResponseContract(SyncResponseContractRequestType request) {
        String operationId = request.getOperationId();
        SyncResponseContractResponseType responseType = new SyncResponseContractResponseType();
        Set<String> entryPointTypes =
                applicationOperationConfigurationRepository.listByOperationId(operationId).getOperationTypes();
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


        // distinct by operationName
        List<CompareResultDto> dependencies =
                new ArrayList<>(replayCompareResultRepository.queryCompareResults(planItemIds, recordIds)
                        .stream()
                        .filter(compareResultDto -> !MockCategoryType.create(compareResultDto.getCategoryName()).isEntryPoint())
                        .collect(Collectors.toMap(
                                CompareResultDto::getOperationName, Function.identity(),
                                (oldValue, newValue) -> oldValue))
                        .values());

        // key:<operationName, operationType>
        Map<Pair<String, String>, List<CompareResultDto>> compareResultMap =
                dependencies.stream().collect(Collectors.groupingBy(compareResultDto ->
                        new ImmutablePair<>(compareResultDto.getOperationName(), compareResultDto.getCategoryName())));

        List<AppContractDto> applicationInfoDtos = new ArrayList<>();
        AppContractDto entryPointApplication = new AppContractDto();
        entryPointApplication.setOperationId(operationId);
        entryPointApplication.setOperationName(latestEntryCompareResult.getOperationName());
        entryPointApplication.setOperationType(latestEntryCompareResult.getCategoryName());
        entryPointApplication.setContract(perceiveContract(latestNEntryCompareResults));
        entryPointApplication.setIsEntryPoint(true);
        applicationInfoDtos.add(entryPointApplication);

        compareResultMap.values().forEach(compareResultDtoList -> {
            CompareResultDto compareResultDto = compareResultDtoList.get(FIRST_INDEX);
            if (CollectionUtils.isNotEmpty(compareResultDtoList)) {
                AppContractDto dependencyApplication = new AppContractDto();
                dependencyApplication.setContract(perceiveContract(compareResultDtoList));
                dependencyApplication.setOperationId(operationId);
                dependencyApplication.setOperationName(compareResultDto.getOperationName());
                dependencyApplication.setOperationType(compareResultDto.getCategoryName());
                dependencyApplication.setIsEntryPoint(false);
                applicationInfoDtos.add(dependencyApplication);
            }
        });
        appContractRepository.saveAppContractList(applicationInfoDtos);

        Map<String, Dependency> dependencyMap = appContractRepository.queryAppContractList(operationId)
                .stream()
                .filter(applicationDto -> !MockCategoryType.create(applicationDto.getOperationType()).isEntryPoint())
                .map(this::buildDependency)
                .collect(Collectors.toMap(Dependency::getDependencyId, Function.identity()));
        responseType.setEntryContractStr(entryPointApplication.getContract());
        responseType.setDependencyMap(dependencyMap);
        return responseType;
    }

    private Pair<String, String> getDependencyKey(CompareResultDto compareResultDto) {
        return new ImmutablePair<>(compareResultDto.getOperationName(), compareResultDto.getCategoryName());
    }

    private Dependency buildDependency(AppContractDto appContractDto) {
        Dependency dependency = new Dependency();
        dependency.setDependencyId(appContractDto.getId());
        dependency.setDependencyName(appContractDto.getOperationName());
        dependency.setDependencyType(appContractDto.getOperationType());
        dependency.setContract(appContractDto.getContract());
        return dependency;
    }

    private String perceiveContract(List<CompareResultDto> compareResultDtoList) {
        // todo:
        return null;
    }


    private void adjustJsonNode(JsonNode node, boolean isArray) {
        JsonNode typeNode = node.get(TYPE);
        if (typeNode == null) {
            return;
        }
        if (typeNode.isValueNode()) {
            String type = typeNode.asText();
            ObjectNode subNode = null;
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
                    JsonNode anyOf = subNode.get("anyOf");
                    if (anyOf != null) {
                        subNode.remove("anyOf");
                        ((ObjectNode) node).set(ITEMS, removeNullNode(anyOf));
                    }
                    adjustJsonNode(node.get(ITEMS), true);
                }
            } else {
                if (isArray && !Objects.equals(type, "null")) {
                    JsonNode oldTypeNode = node.get(TYPE);
                    ObjectNode newNode = JsonNodeFactory.instance.objectNode();
                    ObjectNode newTypeNode = JsonNodeFactory.instance.objectNode();
                    newTypeNode.set(TYPE, JsonNodeFactory.instance.textNode(oldTypeNode.asText()));
                    newNode.set("%value%", newTypeNode);
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
                newNode.set("%value%", newTypeNode);
                ((ObjectNode) node).set(TYPE, JsonNodeFactory.instance.textNode(OBJECT));
                ((ObjectNode) node).set(PROPERTIES, newNode);
            }
        }

    }

    private JsonNode removeNullNode(JsonNode node) {
        ArrayNode arrayNode = (ArrayNode) node;
        int size = arrayNode.size();
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(arrayNode.get(i).get(TYPE).asText(), "null")) {
                return arrayNode.get(i);
            }
        }
        return null;
    }

    private JsonNode removeNullType(JsonNode node) {
        ArrayNode arrayNode = (ArrayNode) node;
        int size = arrayNode.size();
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(arrayNode.get(i).asText(), "null")) {
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
        ObjectNode subNode = null;
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


    public static void main(String[] args) throws IOException {

        String str = "{\"id\":null,\"record\":\"111-222\",\"messages\":[{\"title\":\"title description\"," +
                "\"content\":[{\"p1\":\"cr\",\"p2\":17}]}],\"comment\":[[{\"approve\":false}]],\"visitors\":[]}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(str);
        ObjectNode shema = INFERRER.inferForSample(jsonNode);
        SchemaInferService schemaInferService = new SchemaInferService();
        schemaInferService.getArray(shema);
        System.out.println(mapper.writeValueAsString(shema));
    }

}
