package io.github.aleksas.arduino.simplerpc;

import java.io.InputStream;
import java.io.OutputStream;

public interface Transport {
    public void open();
    public void close();
    public boolean isOpen();
    public InputStream getInputStream();
    public OutputStream getOutputStream();
}
