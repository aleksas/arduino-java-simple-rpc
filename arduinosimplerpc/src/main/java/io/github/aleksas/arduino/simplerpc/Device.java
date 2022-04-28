package io.github.aleksas.arduino.simplerpc;

import java.util.HashMap;

public class Device {
    char endianness = '<';
    HashMap<String, Method> methods = new HashMap<String, Method>();
    String protocol = "";
    char size_t = 'H';
    int[] version = {0, 0, 0};
}
