/******************************************************************************
 * Radio Code Calculator API - WebApi interface
 *
 * Version        : v1.1.6
 * Language       : Java
 * Author         : Bartosz Wójcik
 * Web page       : https://www.pelock.com
 *
 *****************************************************************************/

package com.pelock.radiocode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 * Radio Code Calculator Web API client ({@code login} / {@code calc} / {@code info} / {@code
 * list}).
 */
public class RadioCodeCalculator {

  public static final String DEFAULT_API_URL =
      "https://www.pelock.com/api/radio-code-calculator/v1";

  private static final ObjectMapper JSON = new ObjectMapper();

  private final String apiKey;
  private String apiUrl = DEFAULT_API_URL;

  public RadioCodeCalculator(String apiKey) {
    this.apiKey = apiKey;
  }

  public RadioCodeCalculator() {
    this(null);
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = Objects.requireNonNull(apiUrl, "apiUrl");
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public RadioApiResult login() throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "login");
    JsonNode result = postRequest(params);
    return new RadioApiResult(errorOf(result), result);
  }

  public RadioApiResult calc(RadioModel radioModel, String radioSerialNumber) throws IOException {
    return calc(radioModel, radioSerialNumber, "");
  }

  public RadioApiResult calc(RadioModel radioModel, String radioSerialNumber, String radioExtraData)
      throws IOException {
    return calc(radioModel == null ? null : radioModel.name, radioSerialNumber, radioExtraData);
  }

  public RadioApiResult calc(String radioModel, String radioSerialNumber) throws IOException {
    return calc(radioModel, radioSerialNumber, "");
  }

  public RadioApiResult calc(String radioModel, String radioSerialNumber, String radioExtraData)
      throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "calc");
    params.put("radio_model", radioModel == null ? "" : radioModel);
    params.put("serial", radioSerialNumber == null ? "" : radioSerialNumber);
    params.put("extra", radioExtraData == null ? "" : radioExtraData);
    JsonNode result = postRequest(params);
    return new RadioApiResult(errorOf(result), result);
  }

  public RadioApiResult info(RadioModel radioModel) throws IOException {
    return info(radioModel == null ? null : radioModel.name);
  }

  public RadioApiResult info(String radioModel) throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "info");
    params.put("radio_model", radioModel == null ? "" : radioModel);
    JsonNode result = postRequest(params);
    int error = errorOf(result);
    if (error != RadioErrors.SUCCESS) {
      return new RadioApiResult(error, result, (RadioModel) null);
    }
    RadioModel model = RadioModel.fromInfoResponse(radioModel, result);
    return new RadioApiResult(error, result, model);
  }

  public RadioApiResult list() throws IOException {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("command", "list");
    JsonNode result = postRequest(params);
    int error = errorOf(result);
    if (error != RadioErrors.SUCCESS) {
      return new RadioApiResult(error, result, (List<RadioModel>) null);
    }
    return new RadioApiResult(error, result, parseSupportedRadioModels(result));
  }

  public JsonNode postRequest(Map<String, String> paramsArray) throws IOException {
    LinkedHashMap<String, String> params = new LinkedHashMap<>(paramsArray);
    if (apiKey != null && !apiKey.isEmpty()) {
      params.put("key", apiKey);
    }

    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    for (Map.Entry<String, String> e : params.entrySet()) {
      builder.addTextBody(
          e.getKey(), e.getValue(), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
    }
    HttpEntity entity = builder.build();

    HttpPost post = new HttpPost(apiUrl);
    post.setEntity(entity);
    post.addHeader("User-Agent", "PELock Radio Code Calculator");

    try (CloseableHttpClient http = HttpClients.createDefault();
        CloseableHttpResponse response = http.execute(post)) {
      String body;
      try {
        body =
            response.getEntity() != null
                ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                : "";
      } catch (ParseException e) {
        return connectionError();
      }
      if (body == null || body.isEmpty()) {
        return connectionError();
      }
      JsonNode result = JSON.readTree(body);
      if (result == null || !result.isObject()) {
        return connectionError();
      }
      return result;
    } catch (IOException e) {
      return connectionError();
    }
  }

  static List<RadioModel> parseSupportedRadioModels(JsonNode result) {
    List<RadioModel> models = new ArrayList<>();
    JsonNode supported = result == null ? null : result.get("supportedRadioModels");
    if (supported == null || !supported.isObject()) {
      return models;
    }
    Iterator<Map.Entry<String, JsonNode>> fields = supported.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> prop = fields.next();
      JsonNode jo = prop.getValue();
      if (jo == null || !jo.isObject()) {
        continue;
      }
      int serialLen = jo.path("serialMaxLen").asInt(0);
      int extraLen = jo.path("extraMaxLen").asInt(0);
      Map<String, String> serialMap = RadioModel.regexMapFromJson(jo.get("serialRegexPattern"));
      Map<String, String> extraMap = null;
      if (extraLen != 0 && jo.has("extraRegexPattern")) {
        extraMap = RadioModel.regexMapFromJson(jo.get("extraRegexPattern"));
      }
      models.add(new RadioModel(prop.getKey(), serialLen, serialMap, extraLen, extraMap));
    }
    return models;
  }

  private static int errorOf(JsonNode result) {
    if (result == null || !result.has("error")) {
      return RadioErrors.ERROR_CONNECTION;
    }
    return result.get("error").asInt(RadioErrors.ERROR_CONNECTION);
  }

  private static ObjectNode connectionError() {
    ObjectNode node = JSON.createObjectNode();
    node.put("error", RadioErrors.ERROR_CONNECTION);
    return node;
  }
}
