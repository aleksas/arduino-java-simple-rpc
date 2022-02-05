package com.simplerpc;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public Interface(String device, int baudrate, int wait, boolean autoconnect, InputStream load) throws Exception {
        this.wait = wait;
        this.connection = new Serial(device, true, baudrate); //serial_for_url(device, true, baudrate);

        if (autoconnect)
            this.Open(load);
    }

    private static void AssertProtocol(String protocol) throws Exception {
        if (!protocol.equals(PROTOCOL))
            throw new Exception("Invalid protocol header");
    }

    private static void AssertVersion(int[] version) throws Exception {
        if (!Arrays.equals(version, VERSION)) {
            String vesionString = IntStream.of(version).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(",")) ;
            String refVesionString = IntStream.of(VERSION).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(","));
            throw new Exception(String.format("Version mismatch (device: %s, client: %s)", refVesionString, vesionString)); 
        }
    }

    private void Write(Object object_type, Object value) {
        
    }

    private String ReadByteString() {
        return Io.ReadByteString(connection.GetInputStream());
    }

    /**
     * Read a return value from a remote procedure call.
     * @param obj_type Return type.
     * @return Return type.
     * @throws Exception
     */
    private Object Read(Object obj_type) throws Exception {
        return Io.Read(connection.GetInputStream(), device.endianness, device.size_t, obj_type);
    }

    /**
     * Initiate a remote procedure call, select the method.
     * @param index Method index.
     */
    private void Select(int index) {
        this.Write("B", index);
    } 

    private void Load(InputStream handle) {
        // TODO: implement yaml loader
    }

    /**
     * Get remote procedure call methods.
     * @return Methods.
     * @throws Exception
     */
    private void GetMethods() throws Exception {
        Select(LIST_REQUEST);  
        
        AssertProtocol(ReadByteString());
        device.protocol = PROTOCOL;

        var version = new int[]{
            ((Integer)Read("B")).intValue(),
            ((Integer)Read("B")).intValue(),
            ((Integer)Read("B")).intValue()
        };
        AssertVersion(version);

        device.version = VERSION;

        var endianness_size = ReadByteString();
        device.endianness = endianness_size.charAt(0);
        device.size_t = endianness_size.charAt(1);

        for (int i = 0;; i++) {
            var line = ReadByteString();
            if (line.isEmpty())
                break;

            var buffer = ByteBuffer.wrap(line.getBytes());
            
            var method = Protocol.ParseLine(i, buffer);
            device.methods.put(method.name, method);
        }
    }

    public void Open(InputStream handle) throws Exception {
        try {
            Thread.sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (handle != null)
            Load(handle);
        else
            GetMethods();
            
        for (var method : device.methods.values()){
            // TODO: generate function.
        }
            // setattr(
            //     self, method['name'], MethodType(make_function(method), self))
    }
}
