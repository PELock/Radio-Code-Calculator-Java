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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parse {@code /pattern/flags} strings as used by the PHP SDK and Web API. */
final class RadioRegex {

  private static final Pattern SLASH = Pattern.compile("^/(.*)/([a-z]*)$", Pattern.DOTALL);

  private RadioRegex() {}

  static Pattern fromSlashPattern(String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      return null;
    }
    Matcher m = SLASH.matcher(pattern);
    if (!m.matches()) {
      return Pattern.compile(pattern);
    }
    String body = m.group(1);
    String flags = m.group(2);
    int options = 0;
    if (flags.indexOf('i') >= 0) {
      options |= Pattern.CASE_INSENSITIVE;
    }
    return Pattern.compile(body, options);
  }
}
