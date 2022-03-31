package com.simplerpc;

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
    public void test0PreOpen() throws Exception {
        System.out.println("EXAMPLE");

        Assert.assertFalse(iface.isOpen());
        Assert.assertTrue(iface.device.methods.isEmpty());
    }

    @Test
    public void test1Open() throws Exception {
        iface.open();
        Assert.assertTrue(iface.isOpen());
    }

    @Test
    public void test2Version() throws Exception {
        Assert.assertEquals(iface.device.version, Interface.VERSION);
    }
    
    // public void testPing() {
    //     Assert.assertEquals(this.iface.methods.get("ping"), 3);
    // }
}