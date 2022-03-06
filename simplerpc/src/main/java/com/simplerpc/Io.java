package com.simplerpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Io class.
 */
public class Io {
    /**
     * Select the appropriate casting function given a C type.
     * @param c_type C type.
     * @return Casting function.
     */
    public static Class Cast(char c_type) {
        if (c_type == '?')
            return boolean.class;
        else {
            if (Arrays.asList('c', 's').contains(c_type))
                return byte[].class;
            if (Arrays.asList('f', 'd').contains(c_type))
                return float.class;
            return int.class;
        }
    } 

    private static void WriteBasic(OutputStream stream, char endianness, char basic_type, Object value) throws Exception {
        if (basic_type == 's') {
            stream.write(((String) value).getBytes());
            stream.write((byte) '\0');
            return;
        }


        String full_type = (String.valueOf(endianness) + basic_type);        

        ByteBuffer buffer = ByteBufferStruct.Pack(full_type, Arrays.asList(Cast(basic_type).cast(value)).toArray());
        
        Channels.newChannel(stream).write(buffer);
    }

    /**
     * Read bytes from {stream} until the first encounter of {delimiter}.
     * @param stream Stream object.
     * @param stop Delimiter.
     * @return Byte.
     */
    private static byte[] ReadBytesUntil(InputStream stream, byte stop) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte value;
        do {
            try {
                value = (byte) stream.read();
                if (value == stop) break;
                buffer.write(value);
            } catch (IOException e) {
                break;
            }
        } while (true);
        return buffer.toByteArray();
    }

    public static String ReadByteString(InputStream stream) {
        return new String(ReadBytesUntil(stream, (byte) '\0'), StandardCharsets.UTF_8);
    }

    /**
     * Read a value of basic type from a stream.
     * @param stream Stream object.
     * @param endianness Endianness.
     * @param basic_type Type of {value}.
     * @return Value of type {basic_type}.
     * @throws Exception
     */
    private static Object ReadBasic(InputStream stream, char endianness, char basic_type) {
        if (basic_type == 's')
            return ReadByteString(stream);
        
        String full_type = (String.valueOf(endianness) + basic_type);

        return ByteBufferStruct.Unpack(full_type, stream)[0];
    }

    /**
     * Read an object from a stream.
     * @param stream Stream object.
     * @param endianness Endianness.
     * @param size_t Type of size_t.
     * @param obj_type Type object.
     * @return Object of type {obj_type}.
     * @throws Exception
     */
    public static Object Read(InputStream stream, char endianness, char size_t, Object obj_type) {
        if (obj_type == null) {
            return null;
        } else if (obj_type instanceof Tuple) {
            var tuple = (Tuple) obj_type;
            ArrayList<Object> tmp = new ArrayList<Object>();
            for (var object: tuple.toList())
                tmp.add(Read(stream, endianness, size_t, object));
            return new Tuple(tmp.toArray());
        } else if (obj_type instanceof List){
            Integer length = (Integer) ReadBasic(stream, endianness, size_t);
            ArrayList<Object> tmp = new ArrayList<Object>();
            for (var object: (List) obj_type)
                for (int i = 0; i < length; i++)
                    tmp.add(Read(stream, endianness, size_t, object));
            return Arrays.asList(tmp);
        } else if (obj_type instanceof Object[]) {
            return Read(stream, endianness, size_t, Arrays.asList(obj_type));    
        }

        return ReadBasic(stream, endianness, ((String) obj_type).charAt(0));
    }
}