package io.github.aleksas.arduino.simplerpc;

import static java.util.Map.entry;

import java.io.IOException;
import java.util.Map;

public class Config {
    public static final Map<String,String> DEVICES = Map.ofEntries(
        entry("serial", "/dev/ttyUSB0"),
        entry("wifi", "socket://192.168.21.53:1025"),
        entry("bt", "/dev/rfcomm0")
    );   

    public static final byte[] INTERFACE;
    
    static {
        byte[] iface = null;
        try {
            iface = Config.class.getClassLoader().getResourceAsStream("interface.yaml").readAllBytes();
        } catch (IOException e) {
        } finally {
            INTERFACE = iface;
        }        
    }
}
