package com.pelock.radiocode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RadioModelTest {

  @Test
  void fordMSeriesAcceptsSixDigits() {
    assertEquals(RadioErrors.SUCCESS, RadioModels.FORD_M_SERIES.validate("123456"));
  }

  @Test
  void fordMSeriesRejectsShortSerial() {
    assertEquals(RadioErrors.INVALID_SERIAL_LENGTH, RadioModels.FORD_M_SERIES.validate("1"));
  }

  @Test
  void fordMSeriesRejectsNonDigit() {
    assertEquals(
        RadioErrors.INVALID_SERIAL_PATTERN, RadioModels.FORD_M_SERIES.validate("12345A"));
  }

  @Test
  void renaultDaciaPattern() {
    assertEquals(RadioErrors.SUCCESS, RadioModels.RENAULT_DACIA.validate("Z999"));
    assertEquals(RadioErrors.INVALID_SERIAL_PATTERN, RadioModels.RENAULT_DACIA.validate("9999"));
  }

  @Test
  void eclipseEsnUsesCorrectApiName() {
    assertEquals("eclipse-esn", RadioModels.ECLIPSE_ESN.name);
    assertEquals(RadioErrors.SUCCESS, RadioModels.ECLIPSE_ESN.validate("7D4046"));
  }
}
