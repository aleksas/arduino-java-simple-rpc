package com.simplerpc.serial;

import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class Serial {
    SerialPort comPort;

    public Serial(String device, boolean do_not_open, int baudrate) {
        comPort = SerialPort.getCommPort(device);
        comPort.setBaudRate(baudrate);

        if (!do_not_open)
            comPort.openPort();

        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        // in = comPort.getInputStream();
        // output_stream = comPort.getOutputStream();

        // try {
        //     for (int j = 0; j < 1000; ++j)
        //         System.out.print((char) in.read());
        //     in.close();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // comPort.closePort();
    }

    public void Open() {
        comPort.openPort();
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
