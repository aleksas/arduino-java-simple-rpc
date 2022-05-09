package io.github.aleksas.arduino.simplerpc;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import io.github.aleksas.arduino.simplerpc.serial.Serial;

@DisabledIfEnvironmentVariable(named="CI", matches="true")
public class BluetoothTest extends DeviceTest<Interface> {
    @Override
    protected Interface createInstance() {
        try {
            Serial transport = new Serial(getDevice(), true, 9600);
            return new Interface(transport, 0, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    

    @Override
    protected String getDevice() {
        return Config.DEVICES.get("bt");
    }    
}
