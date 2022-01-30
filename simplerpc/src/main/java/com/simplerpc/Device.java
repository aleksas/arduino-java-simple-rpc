package com.simplerpc;

import java.util.Map;

public class Device {
    char endianness = '<';
    Map<String, Method> methods;
    String protocol = "";
    char size_t = 'H';
    int[] version = {0, 0, 0};
}
