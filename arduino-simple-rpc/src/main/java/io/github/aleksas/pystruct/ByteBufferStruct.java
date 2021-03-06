package io.github.aleksas.pystruct;

import static java.nio.ByteBuffer.wrap;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;

public class ByteBufferStruct {
    private static final String FAILED_TO_READ_TYPE_COMPLETELY = "Failed to read type completely";

    public static Map<Character, ByteOrder> ORDER = Map.of(
        '<', ByteOrder.LITTLE_ENDIAN,
        '>', ByteOrder.BIG_ENDIAN,
        '@', ByteOrder.nativeOrder(),
        '=', ByteOrder.nativeOrder(),
        '!', ByteOrder.BIG_ENDIAN
    );

    public static Map<Character, Integer> TYPE_SIZES = Map.ofEntries(
        Map.entry('c', 1), // char
        Map.entry('b', 1), // signed char
        Map.entry('B', 1), // unsigned char
        Map.entry('?', 1), // Bool
        Map.entry('h', 2), // short
        Map.entry('H', 2), // unsigned short
        Map.entry('i', 4), // int
        Map.entry('I', 4), // unsigned int
        Map.entry('l', 4), // long
        Map.entry('L', 4), // unsigned long
        Map.entry('q', 8), // long long
        Map.entry('Q', 8), // unsigned long long
        Map.entry('f', 4), // float
        Map.entry('d', 8)  // double
    );  
    
    private static BigInteger toUnsignedBigInteger(long i) {
        if (i >= 0L) {
            return BigInteger.valueOf(i);
        } else {
            int upper = (int) (i >>> 32);
            int lower = (int) i;
             // return (upper << 32) + lower
            return BigInteger.valueOf(Integer.toUnsignedLong(upper))
                    .shiftLeft(32)
                    .add(BigInteger.valueOf(Integer.toUnsignedLong(lower)));
        }
    }

    public static Object Cast(char format, Object object) {
        switch (format) {
            case 'c': return (Character) object;
            case 'b': return (byte) object;
            case 'B': return (int) object;
            case '?': return (boolean) object;
            case 'h': 
                if (object instanceof Integer) {
                    return ((Integer)object).shortValue();
                } else if (object instanceof Long) {
                    return ((Long)object).shortValue();
                } else if (object instanceof BigInteger) {
                    return ((BigInteger)object).shortValue();
                }
                return (Short)object;
            case 'H': 
                if (object instanceof Long) {
                    return ((Long)object).intValue();
                } else if (object instanceof BigInteger) {
                    return ((BigInteger)object).intValue();
                }
                return (int) object;
            case 'i':
                if (object instanceof Long) {
                    return ((Long)object).intValue();
                } else if (object instanceof BigInteger) {
                    return ((BigInteger)object).intValue();
                }
                return (int) object;
            case 'I':
            case 'L':
                if (object instanceof BigInteger) {
                    return ((BigInteger)object).longValue();
                }
                return (long) object;
            case 'q': 
            case 'Q': return (BigInteger) object;
            case 'f': 
                if (object instanceof Integer) {
                    return ((Integer)object).floatValue();
                } else if (object instanceof Long) {
                    return ((Long)object).floatValue();
                } else if (object instanceof BigInteger) {
                    return ((BigInteger)object).floatValue();
                }
                return (float) object;
            case 'd':
                if (object instanceof Integer) {
                    return ((Integer)object).doubleValue();
                } else if (object instanceof Long) {
                    return ((Long)object).doubleValue();
                } else if (object instanceof BigInteger) {
                    return ((BigInteger)object).doubleValue();
                }
                return (double) object;
            default:
                throw new RuntimeException(String.format("Not supported format: %c", format));
        }
    }

    public static ByteBuffer Pack(String format, Object[] object) {
        byte[] bytes = new byte[CalcSize(format)];
        
        ByteBuffer buffer = wrap(bytes);
        buffer.rewind();
        buffer.order(GetByteOrder(format));
        
        int offset = 0;

        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (i == 0 && ORDER.keySet().contains(c)) {
                offset = 1;
                continue;
            }

            int pos = i - offset;

            switch (c) {
                case 'c':
                    buffer.put((byte) ((Character) object[pos]).charValue());
                    break;
                case 'b':
                    buffer.put((byte) object[pos]);
                    break;
                case 'B':
                    buffer.put((byte) (((int) object[pos]) & 0xff));
                    break;
                case '?':
                    buffer.put((byte)(((boolean) object[pos])?1:0));
                    break;
                case 'h':
                    buffer.putShort((Short) Cast(c, object[pos]));
                    break;
                case 'H':
                    buffer.putShort((short) (((int) object[pos]) & 0xffff));
                    break;
                case 'i':
                case 'l':
                    buffer.putInt((int) Cast(c, object[pos]));
                    break;
                case 'I':
                case 'L':
                    buffer.putInt((int) (((long) object[pos]) & 0xffffffffL));
                    break;
                case 'q':
                    buffer.putLong((long) Cast(c, object[pos]));
                    break;
                case 'Q':
                    buffer.putLong(((BigInteger) object[pos]).longValue() & 0xffffffffffffffffL);
                    break;
                case 'f':
                    buffer.putFloat((float) Cast(c, object[pos]));
                    break;
                case 'd':
                    buffer.putDouble((float) Cast(c, object[pos]));
                    break;
                default:
                    throw new RuntimeException(String.format("Not supported format: %c", c));
            }
        }

        buffer.rewind();

        return buffer;
    }

    public static Object[] Unpack(String format, InputStream stream) {
        ArrayList<Object> result = new ArrayList<Object>();
        
        byte[] bytes = new byte[CalcSize(format)];
        try {
            int length = stream.read(bytes);
            if (bytes.length != length)
                throw new Exception(FAILED_TO_READ_TYPE_COMPLETELY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ByteBuffer buffer = wrap(bytes);
        buffer.rewind();
        buffer.order(GetByteOrder(format));
        
        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (i == 0 && ORDER.keySet().contains(c))
                continue;
            
            Object o;

            switch (c) {
                case 'c':
                    o = (char)buffer.get();
                    break;
                case 'b':
                    o = buffer.get();
                    break;
                case 'B':
                    o = Byte.toUnsignedInt(buffer.get());
                    break;
                case '?':
                    o = (buffer.get() != 0);
                    break;
                case 'h':
                    o = buffer.getShort();
                    break;
                case 'H':
                    o = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 'i':
                case 'l':
                    o = buffer.getInt();
                    break;
                case 'I':
                case 'L':
                    o = Integer.toUnsignedLong(buffer.getInt());
                    break;
                case 'q':
                    o = buffer.getLong();
                    break;
                case 'Q':
                    o = toUnsignedBigInteger(buffer.getLong());
                    break;
                case 'f':
                    o = buffer.getFloat();
                    break;
                case 'd':
                    o = buffer.getDouble();
                    break;
                default:
                    throw new RuntimeException(String.format("Not supported format: %c", c));
            }

            result.add(o);
        }
        
        return result.toArray();        
    }

    private static ByteOrder GetByteOrder(String format) {
        ByteOrder result = ByteOrder.nativeOrder();
        if (format.length() != 0) {
            char c = format.charAt(0); 
            if (ORDER.keySet().contains(c))
                return ORDER.get(c);
        }
        return result;
    }

    public static int CalcSize(String format) {
        int result = 0;
        for (int i = 0; i < format.length(); i++) {
            if (i == 0 && ORDER.keySet().contains(format.charAt(i)))
                continue;
            result += TYPE_SIZES.get(format.charAt(i));
        }
        return result;
    }
    
}
