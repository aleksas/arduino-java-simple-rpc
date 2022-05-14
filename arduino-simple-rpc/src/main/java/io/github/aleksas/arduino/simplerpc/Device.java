package io.github.aleksas.arduino.simplerpc;

import java.util.HashMap;

public class Device {
    public char endianness = '<';
    public HashMap<String, Method> methods = new HashMap<String, Method>();
    public String protocol = "";
    public char size_t = 'H';
    public int[] version = {0, 0, 0};
}
