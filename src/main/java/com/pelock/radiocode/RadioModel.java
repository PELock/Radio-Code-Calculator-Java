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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Radio model with offline validation of serial / extra input (length + regex).
 */
public final class RadioModel {

  public String name = "";
  public int serialMaxLen = 0;
  public int extraMaxLen = 0;
  public String defaultProgrammingLanguage = "php";

  private Map<String, String> serialRegexPatterns = new LinkedHashMap<>();
  private Map<String, String> extraRegexPatterns = null;

  public RadioModel(
      String name, int serialMaxLen, String serialRegexPattern) {
    this(name, serialMaxLen, serialRegexPattern, 0, null);
  }

  public RadioModel(
      String name,
      int serialMaxLen,
      String serialRegexPattern,
      int extraMaxLen,
      String extraRegexPattern) {
    this.name = name;
    this.serialMaxLen = serialMaxLen;
    this.extraMaxLen = extraMaxLen;
    this.serialRegexPatterns = new LinkedHashMap<>();
    this.serialRegexPatterns.put(defaultProgrammingLanguage, serialRegexPattern);
    if (extraMaxLen != 0 && extraRegexPattern != null && !extraRegexPattern.isEmpty()) {
      this.extraRegexPatterns = new LinkedHashMap<>();
      this.extraRegexPatterns.put(defaultProgrammingLanguage, extraRegexPattern);
    }
  }

  public RadioModel(
      String name,
      int serialMaxLen,
      Map<String, String> serialRegexPatterns,
      int extraMaxLen,
      Map<String, String> extraRegexPatterns) {
    this.name = name;
    this.serialMaxLen = serialMaxLen;
    this.extraMaxLen = extraMaxLen;
    this.serialRegexPatterns =
        serialRegexPatterns == null ? new LinkedHashMap<>() : new LinkedHashMap<>(serialRegexPatterns);
    this.extraRegexPatterns =
        extraRegexPatterns == null ? null : new LinkedHashMap<>(extraRegexPatterns);
  }

  public String serialRegexPattern() {
    return resolvePattern(serialRegexPatterns);
  }

  public String extraRegexPattern() {
    if (extraRegexPatterns == null) {
      return null;
    }
    return resolvePattern(extraRegexPatterns);
  }

  public Map<String, String> getSerialRegexPatterns() {
    return Collections.unmodifiableMap(serialRegexPatterns);
  }

  public Map<String, String> getExtraRegexPatterns() {
    return extraRegexPatterns == null ? null : Collections.unmodifiableMap(extraRegexPatterns);
  }

  /**
   * Validate radio serial number and extra data (if a non-empty extra is provided).
   *
   * @return one of the {@link RadioErrors} values
   */
  public int validate(String serial, String extra) {
    if (serial == null || serial.length() != serialMaxLen) {
      return RadioErrors.INVALID_SERIAL_LENGTH;
    }

    Pattern serialRx = RadioRegex.fromSlashPattern(serialRegexPattern());
    if (serialRx == null || !serialRx.matcher(serial).matches()) {
      return RadioErrors.INVALID_SERIAL_PATTERN;
    }

    if (extra != null && extra.length() > 0) {
      if (extra.length() != extraMaxLen) {
        return RadioErrors.INVALID_EXTRA_LENGTH;
      }
      Pattern extraRx = RadioRegex.fromSlashPattern(extraRegexPattern());
      if (extraRx == null || !extraRx.matcher(extra).matches()) {
        return RadioErrors.INVALID_EXTRA_PATTERN;
      }
    }

    return RadioErrors.SUCCESS;
  }

  public int validate(String serial) {
    return validate(serial, null);
  }

  static Map<String, String> regexMapFromJson(JsonNode node) {
    Map<String, String> dict = new LinkedHashMap<>();
    if (node == null || node.isNull()) {
      return dict;
    }
    if (node.isObject()) {
      node.fields()
          .forEachRemaining(
              e -> {
                if (e.getValue() != null && e.getValue().isValueNode()) {
                  dict.put(e.getKey(), e.getValue().asText());
                }
              });
    } else if (node.isValueNode()) {
      String s = node.asText();
      if (s != null && !s.isEmpty()) {
        dict.put("php", s);
      }
    }
    return dict;
  }

  static RadioModel fromInfoResponse(String radioModel, JsonNode root) {
    int serialLen = readInt(root, "serialMaxLen");
    int extraLen = readInt(root, "extraMaxLen");
    Map<String, String> serialMap = regexMapFromJson(root.get("serialRegexPattern"));
    Map<String, String> extraMap = null;
    if (extraLen != 0 && root.has("extraRegexPattern")) {
      extraMap = regexMapFromJson(root.get("extraRegexPattern"));
    }
    return new RadioModel(radioModel, serialLen, serialMap, extraLen, extraMap);
  }

  private String resolvePattern(Map<String, String> map) {
    if (map == null || map.isEmpty()) {
      return "";
    }
    if (map.containsKey(defaultProgrammingLanguage)) {
      return map.get(defaultProgrammingLanguage);
    }
    for (String key : new String[] {"php", "java", "js", "dotnet"}) {
      if (map.containsKey(key)) {
        return map.get(key);
      }
      String lower = key.toLowerCase(Locale.ROOT);
      for (Map.Entry<String, String> e : map.entrySet()) {
        if (e.getKey() != null && e.getKey().equalsIgnoreCase(lower)) {
          return e.getValue();
        }
      }
    }
    return map.values().iterator().next();
  }

  private static int readInt(JsonNode root, String camel) {
    if (root == null || !root.has(camel) || root.get(camel).isNull()) {
      return 0;
    }
    return root.get(camel).asInt(0);
  }
}
