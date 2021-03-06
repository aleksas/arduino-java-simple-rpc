package io.github.aleksas.arduino.simplerpc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
    protected abstract String getDevice();
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
        assertEquals(iface.device.methods.get("ping").ret.typename, "Integer");
    }

    @Test
    public void test05Fmt() {
        assertEquals(iface.device.methods.get("ping").ret.fmt, "B");
    }

    @Test
    public void test06Param() {
        assertEquals(iface.device.methods.get("ping").parameters.get(0).typename, "Integer");
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
    public void test11Save() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            iface.save(outputStream);

            try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                inputStream.reset();
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.setVisibility(PropertyAccessor.FIELD, Visibility.PUBLIC_ONLY);
                try {
                    Device device = mapper.readValue(inputStream, Device.class);
                    assertEquals(device.methods.get("ping").doc, "Echo a value.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

        try (Serial transport = new Serial(getDevice(), true, 9600)) {
            try (Interface interf = new Interface(transport, 0, false, null)) {
                assertFalse(interf.isOpen());
                interf.open();
                assertTrue(interf.isOpen());
                assertEquals(interf.device.version, Interface.VERSION);
                assertEquals(interf.call_method("ping", 3), 3);
                assertEquals(interf.device.methods.get("ping").ret.typename, "Integer");
            }
        }
    }

    @Test
    public void test15Open() throws Exception {
        assertFalse(iface.isOpen());

        try (Serial transport = new Serial(getDevice(), true, 9600)) {
            try (Interface interf = new Interface(transport, 0, false, null)) {
                assertFalse(interf.isOpen());
                interf.open();
                assertTrue(interf.isOpen());
                assertEquals(interf.device.version, Interface.VERSION);
                assertEquals(interf.call_method("inc", 3), 4);
                assertEquals(interf.call_method("inc", -5), -4);
                assertEquals(interf.call_method("inc", 99), 100);
                assertEquals(interf.device.methods.get("inc").ret.typename, "Integer");
            }
        }
    }

    @Test
    public void test16Open() throws Exception {
        assertFalse(iface.isOpen());

        try (Serial transport = new Serial(getDevice(), true, 9600)) {
            try (Interface interf = new Interface(transport, 0, false, null)) {
                assertFalse(interf.isOpen());
                interf.open();
                assertTrue(interf.isOpen());
                assertEquals(interf.device.version, Interface.VERSION);
                assertTrue((Long)interf.call_method("time") > 0);
                assertTrue((Long)interf.call_method("time") > 0);
                assertTrue((Long)interf.call_method("time") > 0);
                assertEquals(interf.device.methods.get("inc").ret.typename, "Integer");
            }
        }
    }

    @Test
    public void test17OpenLoad() throws Exception {
        try (InputStream targetStream = new ByteArrayInputStream(Config.INTERFACE)) {
            iface.open(targetStream);
        }
        assertEquals(iface.device.methods.get("ping").doc, "Echo a value.");
        assertEquals(iface.device.methods.get("inc"), null);
    }
}
