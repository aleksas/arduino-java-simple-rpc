package io.github.aleksas.arduino.simplerpc;

import static java.util.Map.entry;

import java.util.Map;

public class Config {
    public static final Map<String,String> DEVICES = Map.ofEntries(
        entry("serial", "/dev/ttyACM0"),
        entry("wifi", "socket://192.168.21.53:1025"),
        entry("bt", "/dev/rfcomm0")
    );

    // public static final String INTERFACE = """
    //     endianness: <
    //     methods:
    //       ping:
    //         doc: Echo a value.
    //         index: 0
    //         name: ping
    //         parameters:
    //         - doc: Value.
    //           fmt: B
    //           name: data
    //           typename: int
    //         return:
    //           doc: Value of data.
    //           fmt: B
    //           typename: int
    //     protocol: simpleRPC
    //     size_t: H
    //     version: !!python/tuple
    //     - 3
    //     - 0
    //     - 0
    //     """;       
    
}
