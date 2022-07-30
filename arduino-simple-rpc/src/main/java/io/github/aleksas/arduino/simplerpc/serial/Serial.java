package io.github.aleksas.arduino.simplerpc.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

import io.github.aleksas.arduino.simplerpc.Transport;

public class Serial implements Transport, AutoCloseable {
    public SerialPort serial = null;

    /**
     * @param device Serial device path.
     * @param do_not_open Open later explicitly using open() function
     * @param baudrate Serial device baud rate.
     */
    public Serial(String device, boolean do_not_open, int baudrate) {
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort port: ports) {
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
        serial.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING | SerialPort.TIMEOUT_READ_BLOCKING, 5000, 5000);
    }

    public boolean isOpen() {
        return serial.isOpen();
    }

    public InputStream getInputStream() throws IOException {
        return serial.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return serial.getOutputStream();
    }

    @Override
    public void close() {
        serial.closePort();
    }
}
