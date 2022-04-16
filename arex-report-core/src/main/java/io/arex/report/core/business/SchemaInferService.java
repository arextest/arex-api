package io.arex.report.core.business;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.saasquatch.jsonschemainferrer.JsonSchemaInferrer;
import com.saasquatch.jsonschemainferrer.SpecVersion;
import io.arex.report.core.repository.ReplayCompareResultRepository;
import io.arex.report.model.api.contracts.QueryMsgSchemaRequestType;
import io.arex.report.model.api.contracts.QueryMsgSchemaResponseType;
import io.arex.report.model.api.contracts.QuerySchemaForConfigRequestType;
import io.arex.report.model.dto.CompareResultDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
public class SchemaInferService {

    @Resource
    private ReplayCompareResultRepository repository;

    private static final String TYPE = "type";
    private static final String PROPERTIES = "properties";
    private static final String ITEMS = "items";
    private static final String OBJECT = "object";
    private static final String ARRAY = "array";

    private static final JsonSchemaInferrer INFERRER = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_06)
            .build();


    public QueryMsgSchemaResponseType schemaInfer(QueryMsgSchemaRequestType request) {
        QueryMsgSchemaResponseType response = new QueryMsgSchemaResponseType();
        String msg = null;
        if (request.getId() != null) {
            CompareResultDto dto = repository.queryCompareResultsByObjectId(request.getId());
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
            LOGGER.warn("schemaInfer", e);
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
            LOGGER.warn("schemaInferForConfig", e);
        }
        return response;
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
