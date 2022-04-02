package com.simplerpc;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Ignore;

@Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class DeviceTest<T extends Interface> {
    protected abstract T createInstance();
    private static Interface iface = null;

    @Before
    public void setUp() {
        if (iface == null)
            iface = createInstance();
    }

    @Test
    public void test00PreOpen() throws Exception {
        System.out.println("EXAMPLE");

        Assert.assertFalse(iface.isOpen());
        Assert.assertTrue(iface.device.methods.isEmpty());
    }

    @Test
    public void test01Open() throws Exception {
        iface.open();
        Assert.assertTrue(iface.isOpen());
    }

    @Test
    public void test02Version() throws Exception {
        Assert.assertEquals(iface.device.version, Interface.VERSION);
    }
    
    @Test
    public void test03Ping() throws IOException {
        Assert.assertEquals(iface.call_method("ping", 3), 3);
    }

    @Test
    public void test04Type1() {
        Assert.assertEquals(iface.device.methods.get("ping").ret.tyme_name, "int");
    }

    @Test
    public void test05Fmt() {
        Assert.assertEquals(iface.device.methods.get("ping").ret.fmt, "B");
    }

    @Test
    public void test06Param() {
        Assert.assertEquals(iface.device.methods.get("ping").parameters.get(0).tyme_name, "int");
    }

    @Test
    public void test07Param2() {
        Assert.assertEquals(iface.device.methods.get("ping").parameters.get(0).fmt, "B");
    }

    @Test
    public void test08Param3() {
        Assert.assertEquals(iface.device.methods.get("ping").parameters.get(0).name, "data");
    }

    @Test
    public void test09Doc1() {
        Assert.assertEquals(iface.device.methods.get("ping").doc, "Echo a value.");
    }

    @Test
    public void test10Doc2() {
        Assert.assertEquals(iface.device.methods.get("ping").parameters.get(0).doc, "Value.");
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
        Assert.assertTrue(iface.isOpen());
        Assert.assertTrue(iface.device.methods.size() > 0);
        iface.close();
    }

    @Test
    public void test13PostClose() {
        Assert.assertFalse(iface.isOpen());
        Assert.assertTrue(iface.device.methods.size() == 0);
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