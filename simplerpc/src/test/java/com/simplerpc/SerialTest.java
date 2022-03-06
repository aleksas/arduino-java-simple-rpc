package com.simplerpc;

public class SerialTest extends DeviceTest<Interface> {
    @Override
    protected Interface createInstance() {
        try {
            return new Interface(Config.DEVICES.get("serial"), 9600, 2, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
}
