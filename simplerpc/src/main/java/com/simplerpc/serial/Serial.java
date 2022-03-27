package com.simplerpc.serial;

import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class Serial implements AutoCloseable {
    public SerialPort serial = null;

    public Serial(String device, boolean do_not_open, int baudrate) {
        var ports = SerialPort.getCommPorts();
        for(var port: ports) {
            if (port.getSystemPortPath().equals(device)) {
                serial = port;
                break;
            }
        }

        if (serial == null) {            
            serial = SerialPort.getCommPort(device);
            serial.setBaudRate(baudrate);
            serial.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
        }

        if (!do_not_open)
            serial.openPort();
    }
    public void open() {
        if (!serial.openPort())
            throw new RuntimeException("Could not open port");
    }

    public boolean isOpen() {
        return serial.isOpen();
    }

    public InputStream getInputStream() {
        return serial.getInputStream();
    }

    public OutputStream getOutputStream() {
        return serial.getOutputStream();
    }

    @Override
    public void close() throws Exception {
        serial.closePort();
    }
}
