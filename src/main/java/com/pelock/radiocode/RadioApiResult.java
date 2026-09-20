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
import java.util.Collections;
import java.util.List;

/** Result of a Radio Code Calculator API call (login / calc / info / list). */
public final class RadioApiResult {

  private final int error;
  private final JsonNode body;
  private final RadioModel model;
  private final List<RadioModel> models;

  public RadioApiResult(int error, JsonNode body) {
    this(error, body, null, null);
  }

  public RadioApiResult(int error, JsonNode body, RadioModel model) {
    this(error, body, model, null);
  }

  public RadioApiResult(int error, JsonNode body, List<RadioModel> models) {
    this(error, body, null, models);
  }

  private RadioApiResult(int error, JsonNode body, RadioModel model, List<RadioModel> models) {
    this.error = error;
    this.body = body;
    this.model = model;
    this.models = models;
  }

  public int getError() {
    return error;
  }

  public JsonNode getBody() {
    return body;
  }

  /** Unlock code from a successful {@code calc} response. */
  public String getCode() {
    if (body == null || !body.hasNonNull("code")) {
      return null;
    }
    return body.get("code").asText();
  }

  /** Model parsed from a successful {@code info} response. */
  public RadioModel getModel() {
    return model;
  }

  /** Models parsed from a successful {@code list} response. */
  public List<RadioModel> getModels() {
    return models == null ? Collections.emptyList() : models;
  }

  public boolean isSuccess() {
    return error == RadioErrors.SUCCESS;
  }
}
