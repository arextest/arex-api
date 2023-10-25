package com.arextest.web.model.contract.contracts.config.replay;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * Created by rchen9 on 2023/2/8.
 */
@Data
public class ComparisonSummaryConfiguration {

    private String operationName;
    private List<String> operationTypes;
    /**
     * ignore configuration List<String> stores the absolute path of leaf node Set stores multiple leaf nodes
     */
    private Set<List<String>> exclusionList;

    private List<String> ignoreCategoryTypes;

    /**
     * only compare which leaf nodes List<String> stores the absolute path of leaf node Set stores multiple leaf nodes
     */
    private Set<List<String>> inclusionList;

    /**
     * only compare which leaf nodes List<String> stores the absolute path of leaf node Set stores multiple leaf nodes
     */
    private Set<List<String>> encryptionList;

    /**
     * reference relationship
     *
     * List<String> stores the absolute path of leaf node K： fk node path V： pk node path
     */
    @JsonDeserialize(keyUsing = MapKeyDeserializerUtils.class)
    @JsonSerialize(keyUsing = MapKeySerializerUtils.class)
    private Map<List<String>, List<String>> referenceMap;

    /**
     * array collation K: the absolute path of the array node V: a collection of leaf nodes under K, List<String> stores
     * the relative path
     *
     */
    @JsonDeserialize(keyUsing = MapKeyDeserializerUtils.class)
    @JsonSerialize(keyUsing = MapKeySerializerUtils.class)
    private Map<List<String>, List<List<String>>> listSortMap;

    private Map<String, Object> additionalConfig;

    private static class MapKeyDeserializerUtils extends KeyDeserializer {

        @Override
        public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(s, new TypeReference<List<String>>() {});
        }
    }

    private static class MapKeySerializerUtils extends JsonSerializer<List<String>> {

        @Override
        public void serialize(List<String> stringList, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            String string = objectMapper.writeValueAsString(stringList);
            jsonGenerator.writeFieldName(string);
        }
    }
}
