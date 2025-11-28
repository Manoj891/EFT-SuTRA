package com.fcgo.eft.sutra.configure;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;


@Slf4j
@Service
public class StringToJsonNodeImpl implements StringToJsonNode {
    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonFactory factory = mapper.getFactory();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
    private final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public JsonNode toJsonNode(String jsonData) {
        try {
            return mapper.readTree(factory.createParser(jsonData));
        } catch (IOException e) {
            log.error("{} Converting Json Error: {}", jsonData, e.getMessage());
        }
        return null;
    }

    @Override
    public SimpleDateFormat getYyyyMMddHHmmss() {
        return yyyyMMddHHmmss;
    }

    @Override
    public SimpleDateFormat getYyMMdd() {
        return yyMMdd;
    }

    @Override
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}
