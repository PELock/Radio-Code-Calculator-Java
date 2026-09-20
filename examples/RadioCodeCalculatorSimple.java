/******************************************************************************
 * Radio Code Calculator API - WebApi interface usage example
 *
 * Version        : v1.1.6
 * Language       : Java
 * Author         : Bartosz Wójcik
 * Web page       : https://www.pelock.com
 *
 *****************************************************************************/

import com.pelock.radiocode.RadioApiResult;
import com.pelock.radiocode.RadioCodeCalculator;
import com.pelock.radiocode.RadioErrors;
import com.pelock.radiocode.RadioModels;

public class RadioCodeCalculatorSimple {

  public static void main(String[] args) throws Exception {
    RadioCodeCalculator client = new RadioCodeCalculator("ABCD-ABCD-ABCD-ABCD");
    RadioApiResult result = client.calc(RadioModels.FORD_M_SERIES, "123456");

    switch (result.getError()) {
      case RadioErrors.SUCCESS:
        System.out.println("Radio code is " + result.getCode());
        break;
      case RadioErrors.INVALID_RADIO_MODEL:
        System.out.println("Invalid radio model (not supported)");
        break;
      case RadioErrors.INVALID_SERIAL_LENGTH:
        System.out.println("Invalid serial number length");
        break;
      case RadioErrors.INVALID_SERIAL_PATTERN:
        System.out.println("Invalid serial number regular expression pattern");
        break;
      case RadioErrors.INVALID_LICENSE:
        System.out.println("Invalid license key!");
        break;
      default:
        System.out.println("Unexpected error: " + result.getError());
        break;
    }
  }
}
