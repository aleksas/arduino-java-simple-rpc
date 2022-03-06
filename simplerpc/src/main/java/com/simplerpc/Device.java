package com.simplerpc;

import java.util.Collections;
import java.util.Map;

public class Device {
    char endianness = '<';
    Map<String, Method> methods = Collections.emptyMap();
    String protocol = "";
    char size_t = 'H';
    int[] version = {0, 0, 0};
}
