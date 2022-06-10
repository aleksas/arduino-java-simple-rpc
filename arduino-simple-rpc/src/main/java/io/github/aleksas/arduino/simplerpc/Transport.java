package io.github.aleksas.arduino.simplerpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Transport {
    public void open();
    public void close();
    public boolean isOpen();
    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
    public boolean useWritableByteChannel();
}
