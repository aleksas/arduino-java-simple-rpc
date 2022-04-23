package com.simplerpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.lang.model.util.ElementScanner14;

/**
 * Io class.
 */
public class Io {
    /**
     * Select the appropriate casting function given a C type.
     * @param c_type C type.
     * @return Casting function.
     */
    public static Class TypeClass(char c_type) {
        if (c_type == '?')
            return boolean.class;
        else {
            if (Arrays.asList('c', 's').contains(c_type))
                return Byte[].class;
            if (c_type == 'f')
                return Float.class;
            if (c_type == 'd')
                return Double.class;
            if (c_type == 'h')
                return Short.class;
            return Integer.class;
        }
    } 

    public static void WriteBasic(OutputStream stream, char endianness, char basic_type, Object value) throws Exception {
        if (basic_type == 's') {
            assert (value instanceof byte[]);
            stream.write((byte[])value);
            stream.write((byte) '\0');
            return;
        }


        String full_type = (String.valueOf(endianness) + basic_type);        

        Object[] arr = null;
        if (value.getClass().isArray())
            arr = (Object[]) value;
        else
            arr = Arrays.asList(TypeClass(basic_type).cast(value)).toArray();

        ByteBuffer buffer = ByteBufferStruct.Pack(full_type, arr);
        
        Channels.newChannel(stream).write(buffer);
    }

    /**
     * Read bytes from {stream} until the first encounter of {delimiter}.
     * @param stream Stream object.
     * @param stop Delimiter.
     * @return Byte.
     * @throws IOException
     */
    public static byte[] ReadBytesUntil(InputStream stream, byte stop) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
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
    }

    public static byte[] ReadByteString(InputStream stream) throws IOException {
        return ReadBytesUntil(stream, (byte) '\0');
    }

    /**
     * Read a value of basic type from a stream.
     * @param stream Stream object.
     * @param endianness Endianness.
     * @param basic_type Type of {value}.
     * @return Value of type {basic_type}.
     * @throws IOException
     * @throws Exception
     */
    public static Object ReadBasic(InputStream stream, char endianness, char basic_type) throws IOException {
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
     * @throws IOException
     * @throws Exception
     */
    public static Object Read(InputStream stream, char endianness, char size_t, Object obj_type) throws IOException {
        if (obj_type == null) {
            return null;
        } else if (obj_type instanceof Tuple) {
            var tuple = (Tuple) obj_type;
            var tmp = new ArrayList<Object>();
            for (var object: tuple.toList())
                tmp.add(Read(stream, endianness, size_t, object));
            return new Tuple(tmp.toArray());
        } else if (obj_type instanceof List){
            var leno = ReadBasic(stream, endianness, size_t);
            Integer length = null;
            if (leno instanceof Integer) {
                length = (Integer) leno;
            } else if (leno instanceof Short) {
                length = Integer.valueOf((Short) leno);
            } else 
                throw new RuntimeException("Not implemented");
            
            var tmp = new ArrayList<Object>();
            for (var object: (List) obj_type)
                for (int i = 0; i < length; i++)
                    tmp.add(Read(stream, endianness, size_t, object));
            return tmp;
        } else if (obj_type instanceof Object[]) {
            return Read(stream, endianness, size_t, Arrays.asList(obj_type));    
        }

        var out = ReadBasic(stream, endianness, ((String) obj_type).charAt(0));
        return out;
    }

    /**
     * Write an object to a stream.
     * @param stream Stream object.
     * @param endianness Endianness.
     * @param size_t Type of size_t.
     * @param obj_type Type object.
     * @return Object of type {obj_type}.
     * @throws Exception
     */
    public static void Write(OutputStream stream, char endianness, char size_t, Object obj_type, Object object) throws Exception {
        if (obj_type instanceof List) {
            WriteBasic(stream, endianness, size_t, Short.valueOf((short) Math.floorDiv(((List)object).size(), ((List)obj_type).size())));
        }
        if (obj_type instanceof Iterable) {
            var obj_list = new ArrayList<Object>();
            ((Iterable) object).forEach(obj_list::add);

            var obj_type_list = new ArrayList<Object>();

            for (int i = 0; i < obj_list.size(); i++) {
                ((Iterable) obj_type).forEach(obj_type_list::add);
            }
            
            for (int i = 0; i < obj_list.size(); i++) {
                var item = obj_list.get(i);
                var item_type = obj_type_list.get(i);
                Object items = null;
                if (item_type instanceof List) {
                    if (item instanceof List) 
                        items = item;
                    else if (item instanceof Object[]) {
                        throw new RuntimeException(); // TODO
                        // items = new ArrayList<Object>();   
                        // Collections.addAll((List<Object>) items, ((Object[]) object));  
                    } else
                        throw new RuntimeException(); // TODO
                } else {
                    items = java.lang.reflect.Array.newInstance(item.getClass(), 1);
                    ((Object[]) items)[0] = item;
                }
                Write(stream, endianness, size_t, item_type, items);
            }
        }
        else
              WriteBasic(stream, endianness, ((String) obj_type).charAt(0), object);
    }
}
