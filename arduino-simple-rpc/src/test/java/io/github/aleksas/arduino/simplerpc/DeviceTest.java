package io.github.aleksas.arduino.simplerpc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.github.aleksas.arduino.simplerpc.serial.Serial;

@Disabled
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class DeviceTest<T extends Interface> {
    protected abstract T createInstance();
    private static Interface iface = null;

    @BeforeAll
    public void setUp() {
        if (iface == null)
            iface = createInstance();
    }

    @Test
    public void test00PreOpen() throws Exception {
        System.out.println("EXAMPLE");

        assertFalse(iface.isOpen());
        assertTrue(iface.device.methods.isEmpty());
    }

    @Test
    public void test01Open() throws Exception {
        iface.open();
        assertTrue(iface.isOpen());
    }

    @Test
    public void test02Version() throws Exception {
        assertEquals(iface.device.version, Interface.VERSION);
    }
    
    @Test
    public void test03Ping() throws IOException {
        assertEquals(iface.call_method("ping", 3), 3);
    }

    @Test
    public void test04Type1() {
        assertEquals(iface.device.methods.get("ping").ret.tyme_name, "Integer");
    }

    @Test
    public void test05Fmt() {
        assertEquals(iface.device.methods.get("ping").ret.fmt, "B");
    }

    @Test
    public void test06Param() {
        assertEquals(iface.device.methods.get("ping").parameters.get(0).tyme_name, "Integer");
    }

    @Test
    public void test07Param2() {
        assertEquals(iface.device.methods.get("ping").parameters.get(0).fmt, "B");
    }

    @Test
    public void test08Param3() {
        assertEquals(iface.device.methods.get("ping").parameters.get(0).name, "data");
    }

    @Test
    public void test09Doc1() {
        assertEquals(iface.device.methods.get("ping").doc, "Echo a value.");
    }

    @Test
    public void test10Doc2() {
        assertEquals(iface.device.methods.get("ping").parameters.get(0).doc, "Value.");
    }

    @Test
    public void test11Save() {
        // iface_handle = StringIO()

        // self._interface.save(iface_handle)
        // iface_handle.seek(0)
        // device = load(iface_handle, Loader=FullLoader)
        // assert device['methods']['ping']['doc'] == 'Echo a value.'
    }

    @Test
    public void test12Close() {
        assertTrue(iface.isOpen());
        assertTrue(iface.device.methods.size() > 0);
        iface.close();
    }

    @Test
    public void test13PostClose() {
        assertFalse(iface.isOpen());
        assertTrue(iface.device.methods.size() == 0);
    }

    @Test
    public void test14Open() throws Exception {
        assertFalse(iface.isOpen());

        try (var transport = new Serial(Config.DEVICES.get("serial"), true, 9600)) {
            try (var interf = new Interface(transport, 0, false, null)) {
                assertFalse(interf.isOpen());
                interf.open();
                assertTrue(interf.isOpen());
                assertEquals(interf.device.version, Interface.VERSION);
                assertEquals(interf.call_method("ping", 3), 3);
                assertEquals(interf.device.methods.get("ping").ret.tyme_name, "Integer");
            }
        }
    }

    @Test
    public void test14OpenLoad() {
        // iface_handle = StringIO(_interface)

        // self._interface.open(iface_handle)
        // assert (
        //     self._interface.device['methods']['ping']['doc'] ==
        //     'Echo a value.')
        // assert not self._interface.device['methods'].get('inc', None)
    }
}