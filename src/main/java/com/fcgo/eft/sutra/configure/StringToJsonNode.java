package com.fcgo.eft.sutra.configure;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface StringToJsonNode {
    JsonNode toJsonNode(String jsonData) throws IOException;
}
