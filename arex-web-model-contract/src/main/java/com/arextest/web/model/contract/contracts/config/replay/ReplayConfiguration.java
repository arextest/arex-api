package com.arextest.web.model.contract.contracts.config.replay;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ReplayConfiguration {

    /**
     * @see ScheduleConfiguration
     */
    ScheduleConfiguration scheduleConfiguration;

    /**
     * comparison configuration
     */
    List<ReplayComparisonConfig> replayComparisonConfigs;

    @Data
    public static class ReplayComparisonConfig {
        private String operationId;
        /**
         * ignore configuration
         * List<String> stores the absolute path of leaf node
         * Set stores multiple leaf nodes
         */
        private Set<List<String>> exclusionList;

        /**
         * only compare which leaf nodes
         * List<String> stores the absolute path of leaf node
         * Set stores multiple leaf nodes
         */
        private Set<List<String>> inclusionList;

        /**
         * reference relationship
         *
         * List<String> stores the absolute path of leaf node
         * K： fk node path
         * V： pk node path
         */
        @JsonSerialize(keyUsing = MapKeySerializerUtils.class)
        private Map<List<String>, List<String>> referenceMap;

        /**
         * array collation
         * K: the absolute path of the array node
         * V: a collection of leaf nodes under K, List<String> stores the relative path
         *
         */
        @JsonSerialize(keyUsing = MapKeySerializerUtils.class)
        private Map<List<String>, List<List<String>>> listSortMap;

        private static class MapKeySerializerUtils extends JsonSerializer<List<String>> {

            @Override
            public void serialize(List<String> stringList,
                                  JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();
                String string = objectMapper.writeValueAsString(stringList);
                jsonGenerator.writeFieldName(string);
            }
        }
    }
}