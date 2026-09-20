# Radio Code Calculator — Java SDK

Java client for the [Radio Code Calculator](https://www.pelock.com/products/radio-code-calculator) Web API. Generate car radio unlock codes through `https://www.pelock.com/api/radio-code-calculator/v1`.

## Maven

```xml
<dependency>
  <groupId>com.pelock</groupId>
  <artifactId>radio-code-calculator</artifactId>
  <version>1.1.6</version>
</dependency>
```

This package is not published to Maven Central. Install locally with `mvn install`.

## Usage

```java
import com.pelock.radiocode.RadioCodeCalculator;
import com.pelock.radiocode.RadioErrors;
import com.pelock.radiocode.RadioModels;
import com.pelock.radiocode.RadioApiResult;

RadioCodeCalculator client = new RadioCodeCalculator("YOUR-WEB-API-KEY");
RadioApiResult result = client.calc(RadioModels.FORD_M_SERIES, "123456");

if (result.getError() == RadioErrors.SUCCESS) {
    System.out.println("Radio code is " + result.getCode());
}
```

Offline validation (length + regex) is available on `RadioModel.validate(serial, extra)` before you call the API.

See `examples/`. Apache-2.0. Copyright Bartosz Wójcik / PELock.
