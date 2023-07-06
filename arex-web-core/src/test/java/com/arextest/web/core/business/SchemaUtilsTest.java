package com.arextest.web.core.business;

import com.arextest.web.core.business.util.SchemaUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SchemaUtils.class)
public class SchemaUtilsTest {
    private static final String JSON1 = "{\n" +
            "  \"body\": [\n" +
            "    {\n" +
            "      \"id\": 463,\n" +
            "      \"name\": \"Kite\",\n" +
            "      \"age\": 19\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 465,\n" +
            "      \"name\": \"c\",\n" +
            "      \"age\": 20,\n" +
            "      \"gender\": \"male\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"lists\": [\n" +
            "    [\n" +
            "      {\n" +
            "        \"id\": 111,\n" +
            "        \"name\": \"wilde\",\n" +
            "        \"age\": 24\n" +
            "      }\n" +
            "    ],\n" +
            "    [\n" +
            "      {\n" +
            "        \"test\": 0.2\n" +
            "      }\n" +
            "    ]\n" +
            "  ],\n" +
            "  \"responsestatustype \": {\n" +
            "    \"timestamp \": 1687943302747,\n" +
            "    \"responsecode \": 0,\n" +
            "    \"responsedesc \": \"success \"\n" +
            "  }\n" +
            "}";

    private static final String JSON2 = "{\n" +
            " \"body\": [\n" +
            " {\n" +
            " \"id\": 463,\n" +
            " \"name\": \"Kite\",\n" +
            " \"age\": 19\n" +
            " },\n" +
            " {\n" +
            " \"id\": 465,\n" +
            " \"name\": \"Jack\",\n" +
            " \"age\": 20,\n" +
            " \"map\":{\n" +
            " \"int\":111,\n" +
            " \"double\":0.1\n" +
            " }\n" +
            " }\n" +
            " ]\n" +
            "}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Test
    public void test() throws JsonProcessingException {
        String contract = SchemaUtils.mergeJson(null, JSON1);
        Map<String, Object> contractMap = OBJECT_MAPPER.readValue(contract, Map.class);

        Assert.assertEquals(3, contractMap.entrySet().size());
        Assert.assertEquals(4, ((Map)((List)((List) contractMap.get("lists")).get(0)).get(0)).entrySet().size());
        Assert.assertEquals(4, ((Map)((List) contractMap.get("body")).get(0)).entrySet().size());

        Map<String, Object> newModel = OBJECT_MAPPER.readValue(JSON2, Map.class);
        SchemaUtils.mergeMap(contractMap, newModel);
        Assert.assertEquals(5, ((Map)((List) contractMap.get("body")).get(0)).entrySet().size());
    }
}
