package com.fcgo.eft.sutra.configure;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class StringToJsonNodeImpl implements StringToJsonNode {
    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonFactory factory = mapper.getFactory();

    @Override
    public JsonNode toJsonNode(String jsonData) {
        try {
            return mapper.readTree(factory.createParser(jsonData));
        } catch (IOException e) {
            log.error("{} Converting Json Error: {}", jsonData, e.getMessage());
        }
        return null;

    }
}
