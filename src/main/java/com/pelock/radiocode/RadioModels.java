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

/**
 * Built-in radio models with offline validation metadata (lengths and regex patterns).
 *
 * <p>{@link #ECLIPSE_ESN} uses the API name {@code eclipse-esn} (the PHP SDK currently lists this
 * model as {@code toyota-erc}, which is a copy-paste error).
 */
public final class RadioModels {

  public static final RadioModel RENAULT_DACIA =
      new RadioModel("renault-dacia", 4, "/^([A-Z]{1}[0-9]{3})$/");
  public static final RadioModel CHRYSLER_PANASONIC_TM9 =
      new RadioModel("chrysler-panasonic-tm9", 4, "/^([0-9]{4})$/");
  public static final RadioModel CHRYSLER_DODGE_VP =
      new RadioModel("chrysler-dodge-vp", 4, "/^([a-zA-Z0-9]{4})$/");
  public static final RadioModel FORD_M_SERIES =
      new RadioModel("ford-m-series", 6, "/^([0-9]{6})$/");
  public static final RadioModel FORD_V_SERIES =
      new RadioModel("ford-v-series", 6, "/^([0-9]{6})$/");
  public static final RadioModel FORD_TRAVELPILOT =
      new RadioModel("ford-travelpilot", 7, "/^([0-9]{7})$/");
  public static final RadioModel FIAT_STILO_BRAVO_VISTEON =
      new RadioModel("fiat-stilo-bravo-visteon", 6, "/^([a-zA-Z0-9]{6})$/");
  public static final RadioModel FIAT_DAIICHI =
      new RadioModel("fiat-daiichi", 4, "/^([0-9]{4})$/");
  public static final RadioModel FIAT_VP = new RadioModel("fiat-vp", 4, "/^([0-9]{4})$/");
  public static final RadioModel TOYOTA_ERC =
      new RadioModel("toyota-erc", 16, "/^([a-zA-Z0-9]{16})$/");
  public static final RadioModel JEEP_CHEROKEE =
      new RadioModel("jeep-cherokee", 14, "/^([a-zA-Z0-9]{10}[0-9]{4})$/");
  public static final RadioModel NISSAN_GLOVE_BOX =
      new RadioModel("nissan-glove-box", 12, "/^([a-zA-Z0-9]{12})$/");
  public static final RadioModel ECLIPSE_ESN =
      new RadioModel("eclipse-esn", 6, "/^([a-zA-Z0-9]{6})$/");
  public static final RadioModel JAGUAR_ALPINE =
      new RadioModel("jaguar-alpine", 5, "/^([0-9]{5})$/");

  private RadioModels() {}

  /** Return the given model (identity helper matching PHP {@code RadioModels::get}). */
  public static RadioModel get(RadioModel radioModel) {
    return radioModel;
  }
}
