package org.example;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;

@Controller
public class OpenAPISpecController {
  @Value("${openapi.file")
  private String openapiFile;

  // because of our CustomContentNegotiation filter, this needs to return JSON
  @GetMapping(value = "${openapiPath}", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody String getOpenApiSpec() throws IOException {
    Resource resource = new ClassPathResource(openapiFile);
    String data = new String(resource.getInputStream().readAllBytes());
    if (openapiFile.contains(".yaml") || openapiFile.contains(".yml")) {
      return convertYamlToJson(data);
    } else if (openapiFile.contains(".json")) {
      return data;
    }
    throw new Error("Invalid openapi spec format, must be one of [.yaml, .yml, .json]");
  }

  private String convertYamlToJson(String data) throws JsonProcessingException {
    Yaml yaml = new Yaml();
    Map<Object, Object> document = yaml.load(data);

    ObjectMapper jsonWriter = new ObjectMapper();
    return jsonWriter.writeValueAsString(document);
  }
}