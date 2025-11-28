package com.fcgo.eft.sutra.configure;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.SimpleDateFormat;


public interface StringToJsonNode {
    JsonNode toJsonNode(String jsonData);

    SimpleDateFormat getDateFormat();

    SimpleDateFormat getYyMMdd();

    SimpleDateFormat getYyyyMMddHHmmss();
}
