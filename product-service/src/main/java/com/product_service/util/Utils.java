package com.product_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product_service.dto.ProductValidationRequest;
import com.product_service.dto.ProductValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Utils {


    @Autowired
    private ObjectMapper objectMapper;

    public <T> String convertObjectToJson(T request) throws JsonProcessingException {
        String json = null;
        try {
            json = objectMapper.writer().writeValueAsString(request);
        } catch (Exception e) {
            throw e;
        }
        return json;
    }

    public <T> T convertJsonToObject(String jsonString, Class<T> typeClass) throws JsonProcessingException {
        T object = null;
        try {
            object = objectMapper.reader().forType(typeClass).readValue(jsonString);
        } catch (Exception e) {
            throw e;
        }
        return object;
    }

    public <T> T convertJsonToList(String jsonString, TypeReference<T> typeReference) throws JsonProcessingException {
        T object = null;
        try {
            object = objectMapper.readValue(jsonString, typeReference);
        } catch (Exception e) {
            throw e;
        }
        return object;
    }
}
