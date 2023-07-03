package com.arextest.web.api.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableOpenApi
@EnableAsync
// @EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@SpringBootApplication(scanBasePackages = "com.arextest.web")
public class WebSpringBootServletInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebSpringBootServletInitializer.class, args);
        System.out.println(syncResponseContract());
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSpringBootServletInitializer.class);
    }

    private static String syncResponseContract() {
        Map<String, Object> contract = new HashMap<>();

        Map<String, Object> map = new Gson().fromJson(responseJson1, Map.class);


        return JSONObject.valueToString(contract);
    }

    private static final String responseJson1 = "{\"body\":[{\"id\":463,\"name\":\"Kite\",\"age\":19},{\"id\":465," +
            "\"name\":\"Jack\",\"age\":20}],\"responsestatustype\":{\"timestamp\":1687943302747,\"responsecode\":0," +
            "\"responsedesc\":\"success\"}}";

    private static final String responseJson2 = "{\n" +
            "  \"body\": [\n" +
            "    {\n" +
            "      \"id\": 463,\n" +
            "      \"name\": \"Kite\",\n" +
            "      \"age\": 19\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 465,\n" +
            "      \"name\": \"Jack\",\n" +
            "      \"age\": 20,\n" +
            "      \"map\":{\n" +
            "        \"int\":111,\n" +
            "        \"double\":0.1\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

}
