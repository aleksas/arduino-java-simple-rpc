package com.simplerpc.serial;

import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;
import com.simplerpc.Transport;

public class Serial implements Transport, AutoCloseable {
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
        }

        if (!do_not_open)
            serial.openPort();
    }
    
    public void open() {
        if (!serial.openPort())
            throw new RuntimeException("Could not open port");
        serial.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING | SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 5000, 5000);
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
    public void close() {
        serial.closePort();
    }
}
