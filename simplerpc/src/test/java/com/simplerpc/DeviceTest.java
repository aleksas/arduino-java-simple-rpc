package com.simplerpc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class DeviceTest<T extends Interface> {
    protected T iface = null;

    protected abstract T createInstance();
    
    @Before 
    public void setUp() {
        if (iface == null)
            iface = createInstance();
        var prts = this.iface.connection.comPort.getCommPorts();
        var prts2 = this.iface.connection.comPort.getCommPorts();


    }

    @Test
    public void testPreOpen() {
        Assert.assertFalse(this.iface.isOpen());
        Assert.assertTrue(this.iface.device.methods.isEmpty());

        var prts = this.iface.connection.comPort.getCommPorts();
        var prts2 = this.iface.connection.comPort.getCommPorts();
    }

    @Test
    public void testOpen() {
        try {
            this.iface.Open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(this.iface.isOpen());
        Assert.assertFalse(this.iface.device.methods.isEmpty());
    }

    @Test
    public void testVersion() {
        Assert.assertEquals(this.iface.device.version, Interface.VERSION);
    }
    
    // public void testPing() {
    //     Assert.assertEquals(this.iface.methods.get("ping"), 3);
    // }
}