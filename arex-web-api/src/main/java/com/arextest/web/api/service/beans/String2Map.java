package com.arextest.web.api.service.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class String2Map implements Converter<String, Map> {


    @Autowired
    @Lazy
    private ObjectMapper objectMapper;

    @Override
    public Map convert(String source) {
        Map result = null;
        try {
            result = objectMapper.readValue(source, Map.class);
        } catch (JsonProcessingException e) {
        }
        return result;
    }
}