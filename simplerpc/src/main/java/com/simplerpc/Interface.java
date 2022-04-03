package com.simplerpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Interface  implements AutoCloseable {
    private static String PROTOCOL = "simpleRPC";
    private static int LIST_REQUEST = 0xff;
    
    public static int[] VERSION = {3, 0, 0};

    public int wait = 2;
    public boolean autoconnect = true;
    public InputStream load = null; 
    public Transport transport = null; 
    //public int baudrate = 9600;
    public Device device = null; 

    public Interface(Transport transport, int wait, boolean autoconnect, InputStream load) throws Exception {
        this.wait = wait;
        this.transport = transport;//new Serial(device, true, baudrate); //serial_for_url(device, true, baudrate);
        this.device = new Device();

        if (autoconnect)
            this.open(load);
    }

    private static void AssertProtocol(String protocol) {
        if (!protocol.equals(PROTOCOL))
            throw new RuntimeException("Invalid protocol header");
    }

    private static void AssertVersion(int[] version) {
        if (!Arrays.equals(version, VERSION)) {
            String vesionString = IntStream.of(version).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(",")) ;
            String refVesionString = IntStream.of(VERSION).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(","));
            throw new RuntimeException(String.format("Version mismatch (device: %s, client: %s)", refVesionString, vesionString)); 
        }
    }

    public boolean isOpen() {
        return this.transport.isOpen();
    }

    private void write(String format, Object value) throws IOException {
        try (var stream  = transport.getOutputStream()) {
            try (var channel = Channels.newChannel(stream)) {
                var buffer = ByteBufferStruct.Pack(format, new Object[]{ value });

                try {
                    channel.write(buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private byte[] readByteString() throws IOException {
        return Io.ReadByteString(transport.getInputStream());
    }

    /**
     * Read a return value from a remote procedure call.
     * @param obj_type Return type.
     * @return Return type.
     * @throws IOException
     * @throws Exception
     */
    private Object read(Object obj_type) throws IOException {
        return Io.Read(transport.getInputStream(), device.endianness, device.size_t, obj_type);
    }

    /**
     * Initiate a remote procedure call, select the method.
     * @param index Method index.
     * @throws IOException
     * @throws Exception
     */
    private void select(int index) throws IOException {
        this.write("B", index);
    } 

    private void load(InputStream handle) {
        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        // Interface iterface = mapper.readValue(handle, Interface.class);
        // TODO: implement yaml loader
        throw new RuntimeException("Not implementd");
    }

    /**
     * Get remote procedure call methods.
     * @return Methods.
     * @throws IOException
     * @throws Exception
     */
    private void getMethods() throws IOException {
        select(LIST_REQUEST);  

        AssertProtocol(new String(readByteString(), StandardCharsets.UTF_8));
        device.protocol = PROTOCOL;

        var version = new int[]{
            ((Integer)read("B")).intValue(),
            ((Integer)read("B")).intValue(),
            ((Integer)read("B")).intValue()
        };
        AssertVersion(version);

        device.version = VERSION;

        var endianness_size = readByteString();
        device.endianness = (char) endianness_size[0];
        device.size_t = (char) endianness_size[1];

        for (int i = 0;; i++) {
            var line = readByteString();
            if (line.length == 0)
                break;

            var buffer = ByteBuffer.wrap(line);
            
            var method = Protocol.ParseLine(i, buffer);
            device.methods.put(method.name, method);
        }
    }

    /**
     * Connect to device.
     * @throws Exception
     */
    public void open() throws Exception {
        open(null);
    }

    /**
     * Connect to device.
     * @param handle Open file handle.
     * @throws Exception
     */
    public void open(InputStream handle) throws Exception {
        try {
            Thread.sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        transport.open();

        if (handle != null)
            load(handle);
        else
            getMethods();
    }

    
    /**
     * Disconnect from device.
     */
    @Override
    public void close() {
        if (transport != null)
            transport.close();
        device.methods.clear();
    }

    /**
     * Execute a method.
     * @param methodName Method name.
     * @param parameters Method parameters.
     * @return Return value of the method.
     * @throws IOException
     */
    public Object call_method(String methodName, Object... arguments) throws IOException {
        if (!device.methods.containsKey(methodName))
            throw new RuntimeException("Invalid method name: %s.".formatted(methodName));
        
        var method = device.methods.get(methodName);
        var args = Arrays.asList(arguments);

        if (method.parameters.size() != args.size())
            throw new RuntimeException("%s expected %d arguments, got %d.".formatted(methodName, method.parameters.size(), args.size()));
        
        // Call the method.
        this.select(method.index);
        
        // Provide parameters (if any).
        if (method.parameters.size() != 0) {
            for (int i = 0; i < method.parameters.size(); i++) {
                this.write((String)method.parameters.get(i).fmt, args.get(i));
            }
        }

        // Read return value (if any).
        if (method.ret.fmt != null) {
            return this.read((String) method.ret.fmt);
        }

        return null;
    }

    /**
     * Save the interface definition to a file.
     * @param stream Output file stream.
     */
    public void save(OutputStream stream) {
        throw new RuntimeException("Not implemented.");
    }
}
