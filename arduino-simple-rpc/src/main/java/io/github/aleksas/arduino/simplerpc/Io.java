package io.github.aleksas.arduino.simplerpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.aleksas.pystruct.ByteBufferStruct;


/**
 * Io class.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
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
            if (c_type == 'H' || c_type == 'h')
                return Short.class;
            if (c_type == 'i' || c_type == 'I')
                return Integer.class;
            if (c_type == 'L')
                return Long.class;
            if (c_type == '?')
                return Boolean.class;
            return Integer.class;
        }
    } 

    /**
     * @param stream OutputStream
     * @param endianness Endianness
     * @param basic_type Basic type
     * @param value Value
     * @throws IOException
     */
    public static void WriteBasic(OutputStream stream, char endianness, char basic_type, Object value) throws IOException {
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
            //arr = Arrays.asList(TypeClass(basic_type).cast(value)).toArray();
            arr = Arrays.asList(ByteBufferStruct.Cast(basic_type, value)).toArray();

        ByteBuffer buffer = ByteBufferStruct.Pack(full_type, arr);
        
        stream.write(buffer.array());
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
     */
    public static Object Read(InputStream stream, char endianness, char size_t, Object obj_type) throws IOException {
        if (obj_type == null) {
            return null;
        } else if (obj_type instanceof Tuple) {
            Tuple tuple = (Tuple) obj_type;
            List<Object> tmp = new ArrayList<Object>();
            for (Object object: tuple.toList())
                tmp.add(Read(stream, endianness, size_t, object));
            return new Tuple(tmp.toArray());
        } else if (obj_type instanceof List){
            Object leno = ReadBasic(stream, endianness, size_t);
            Integer length = null;
            if (leno instanceof Integer) {
                length = (Integer) leno;
            } else if (leno instanceof Short) {
                length = Integer.valueOf((Short) leno);
            } else 
                throw new RuntimeException("Not implemented");
            
                List<Object> tmp = new ArrayList<Object>();
            for (Object object: (List) obj_type)
                for (int i = 0; i < length; i++)
                    tmp.add(Read(stream, endianness, size_t, object));
            return tmp;
        } else if (obj_type instanceof Object[]) {
            return Read(stream, endianness, size_t, Arrays.asList(obj_type));    
        }

        Object out = ReadBasic(stream, endianness, ((String) obj_type).charAt(0));
        return out;
    }

    /**
     * Write an object to a stream.
     * @param stream Stream object.
     * @param endianness Endianness.
     * @param size_t Type of size_t.
     * @param obj_type Type object.
     * @throws IOException
     */
    
    public static void Write(OutputStream stream, char endianness, char size_t, Object obj_type, Object object) throws IOException {
        if (obj_type instanceof List) {
            WriteBasic(stream, endianness, size_t, Integer.valueOf((int) Math.floorDiv(((List)object).size(), ((List)obj_type).size())));
        }
        if (obj_type instanceof Iterable) {
            List<Object> obj_list = new ArrayList<Object>();
            ((Iterable) object).forEach(obj_list::add);

            List<Object> obj_type_list = new ArrayList<Object>();

            for (int i = 0; i < obj_list.size(); i++) {
                ((Iterable) obj_type).forEach(obj_type_list::add);
            }
            
            for (int i = 0; i < obj_list.size(); i++) {
                Object item = obj_list.get(i);
                Object item_type = obj_type_list.get(i);
                Object items = null;
                if (item_type instanceof Iterable) {
                    items = item;
                } else if (item_type == "s") {
                    items = item;
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
