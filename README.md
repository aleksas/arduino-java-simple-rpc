
Arduino simpleRPC API JAVA client library
---
[![workflow](https://github.com/aleksas/java-simple-rpc/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/aleksas/java-simple-rpc/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.aleksas/arduino-simple-rpc.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.aleksas%22%20AND%20a:%22arduino-simple-rpc%22)
[![javadoc](https://javadoc.io/badge2/io.github.aleksas/arduino-simple-rpc/javadoc.svg)](https://javadoc.io/doc/io.github.aleksas/arduino-simple-rpc)


----

This library provides a simple way to interface to Arduino_ functions exported
with the simpleRPC_ protocol. The exported method definitions are communicated
to the host, which is then able to generate an API interface using this
library.

**Features:**

- User friendly API library.
- Function and parameter names are defined on the Arduino.
- API documentation is defined on the Arduino.
- Support for disconnecting and reconnecting.
- Support for serial and ethernet devices.

Please see javadoc for the latest documentation.


Quick start
-----------

Export any function e.g., ``digitalRead()`` and ``digitalWrite()`` on the
Arduino, these functions will show up as member functions of the ``Interface``
class instance.

First, we make an ``Interface`` class instance and tell it to connect to the
serial device ``/dev/ttyACM0``. All exposed methods can be called like any other class method.

```java
        try (Serial transport = new Serial("/dev/ttyACM0", true, 9600)) {
            try (Interface interf = new Interface(transport, 2, true, null)) {
                System.out.println(interf.call_method("digitalRead", 8)); // Read from pin 8.
                interf.call_method("digitalWrite", 13, true);  // Turn LED on.
            }
        }

```
