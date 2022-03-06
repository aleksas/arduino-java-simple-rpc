package com.simplerpc.serial;

import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class Serial {
    public SerialPort comPort = null;

    public Serial(String device, boolean do_not_open, int baudrate) {
        var ports = SerialPort.getCommPorts();
        for(var port: ports) {
            if (port.getSystemPortPath().equals(device)) {
                comPort = port;
                break;
            }
        }

        if (comPort == null) {            
            comPort = SerialPort.getCommPort(device);
            comPort.setBaudRate(baudrate);
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        }

        if (!do_not_open)
            comPort.openPort();
    }

    public void Open() {
        comPort.openPort();
    }

    public boolean isOpen() {
        return comPort.isOpen();
    }

    public void Close() {
        comPort.closePort();
    }

    public InputStream GetInputStream() {
        return comPort.getInputStream();
    }

    public OutputStream GetOutputStream() {
        return comPort.getOutputStream();
    }
}
