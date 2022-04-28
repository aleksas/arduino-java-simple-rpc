package io.github.aleksas.simplerpc;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import io.github.aleksas.simplerpc.serial.Serial;

@DisabledIfEnvironmentVariable(named="CI", matches="true")
public class SerialTest extends DeviceTest<Interface> {
    @Override
    protected Interface createInstance() {
        try {
            var transport = new Serial(Config.DEVICES.get("serial"), true, 9600);
            return new Interface(transport, 0, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
}
