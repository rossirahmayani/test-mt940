package com.rossi.testmt940.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JsonUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public <T> T convertJson(String json, Class<T> type) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            return objectMapper.readValue(json, type);
        } catch (IOException ex) {
            log.debug("Failed to convert JSON string to object", ex);
        }
        return null;
    }

    public String toJsonString(Object object) {
        try {
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.debug("Failed to convert object to JSON string", ex);
        }
        return null;
    }
}
