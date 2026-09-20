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

/** Errors returned by the Radio Code Calculator API interface. */
public final class RadioErrors {

  public static final int ERROR_CONNECTION = -1;
  public static final int SUCCESS = 0;
  public static final int INVALID_INPUT = 1;
  public static final int INVALID_COMMAND = 2;
  public static final int INVALID_RADIO_MODEL = 3;
  public static final int INVALID_SERIAL_LENGTH = 4;
  public static final int INVALID_SERIAL_PATTERN = 5;
  public static final int INVALID_SERIAL_NOT_SUPPORTED = 6;
  public static final int INVALID_EXTRA_LENGTH = 7;
  public static final int INVALID_EXTRA_PATTERN = 8;
  public static final int INVALID_LICENSE = 100;

  private RadioErrors() {}
}
