package com.fcgo.eft.sutra.configure;

import com.fasterxml.jackson.databind.JsonNode;

public interface StringToJsonNode {
    JsonNode toJsonNode(String jsonData);
}
