package com.simplerpc;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.simplerpc.serial.Serial;

public class Interface {
    private static String PROTOCOL = "simpleRPC";
    private static int[] VERSION = {3, 0, 0};
    private static int LIST_REQUEST = 0xff;

    public int baudrate = 9600;
    public int wait = 2;
    public boolean autoconnect = true;
    public InputStream load = null; 
    public Serial connection;
    public Device device;

    public Interface(String device, int baudrate, int wait, boolean autoconnect, InputStream load) {
        this.wait = wait;
        this.connection = new Serial(device, true, baudrate); //serial_for_url(device, true, baudrate);

        
    }

    private static void AssertProtocol(String protocol) {
        if (!protocol.equals(PROTOCOL))
            throw new Exception("Invalid protocol header");
    }

    private static void AssertVersion(int[] version) {
        if (!Arrays.equals(version, VERSION)) {
            String vesionString = IntStream.of(version).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(",")) ;
            String refVesionString = IntStream.of(VERSION).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(","));
            throw new Exception(String.format("Version mismatch (device: %s, client: %s)", refVesionString, vesionString)); 
        }
    }

    private void Write(Object object_type, Object value) {
        
    }

    private Object Read() {
        return new Object();
    }

    /**
     * Initiate a remote procedure call, select the method.
     * @param index Method index.
     */
    private void Select(int index) {

    } 

    private void Load(InputStream handle) {
        // TODO: implement yaml loader
    }

    private String ReadByteString() {
        return Io.ReadByteString(connection.GetInputStream());
    }

    private Object Read(Object obect_type) {
        return Io.Read(connection.GetInputStream(), device.endianness, device.size_t, obect_type);
    }

    /**
     * Get remote procedure call methods.
     * @return Methods.
     */
    private Method[] GetMethods() {
        Select(LIST_REQUEST);  
        
        AssertProtocol(ReadByteString());
        device.protocol = PROTOCOL;


    }
            """Get remote procedure call methods."""
        self._select(_list_req)

        _assert_protocol(self._read_byte_string().decode())
        self.device['protocol'] = _protocol

        version = tuple(self._read('B') for _ in range(3))
        _assert_version(version)
        self.device['version'] = version

        self.device['endianness'], self.device['size_t'] = (
            chr(c) for c in self._read_byte_string())

        for index, line in enumerate(
                until(lambda x: x == b'', self._read_byte_string)):
            method = parse_line(index, line)
            self.device['methods'][method['name']] = method


    public void Open(InputStream handle) {
        try {
            Thread.sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (handle != null)
            Load(handle);
        else
            self._get_methods()
        for method in self.device['methods'].values():
            setattr(
                self, method['name'], MethodType(make_function(method), self))
    }
}
