package dev.datageneration.jsonHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonParser {


    public static List<JsonNode> parseToJsonNodeSet(List<JSONObject> data, ObjectMapper mapper) {
        return data.stream()
                .map(obj -> {
                    try {
                        return mapper.readTree(obj.toString());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }


    public static List<JsonNode> parseToJsonNodeList(List<JSONObject> data, ObjectMapper mapper) {
        return data.stream()
                .map(obj -> {
                    try {
                        return mapper.readTree(obj.toString());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
