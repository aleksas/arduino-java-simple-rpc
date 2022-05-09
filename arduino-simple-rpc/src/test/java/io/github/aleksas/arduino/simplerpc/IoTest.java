package io.github.aleksas.arduino.simplerpc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.cedarsoftware.util.DeepEquals;

import org.junit.jupiter.api.Test;

public class IoTest {
    private void testInvarianceBasic(char endianness, char basic_type, byte[] data, Object value) throws Exception{
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            Object val = Io.ReadBasic(stream, endianness, basic_type);

            if (val instanceof byte[] && value instanceof byte[]) {
                assertArrayEquals((byte[])val, (byte[])value);
            } else {
                assertEquals(val, value);
            }
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Io.WriteBasic(stream, endianness, basic_type, value);
            assertArrayEquals(stream.toByteArray(), data);
        }
    }

    private void testInvariance(char endianness, char size_t, Object object_def, byte[] data, Object object) throws Exception{
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            Object obj = Io.Read(stream, endianness, size_t, object_def);

            DeepEquals.deepEquals(obj, object, Map.of(DeepEquals.IGNORE_CUSTOM_EQUALS, Set.of(Tuple.class)));
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Io.Write(stream, endianness, size_t, object_def, object);
            assertArrayEquals(stream.toByteArray(), data);
        }
    }

    @Test
    public void testReadBytesUntil() throws Exception {
        try (ByteArrayInputStream stream = new ByteArrayInputStream("abcdef\0abc".getBytes())) {
            byte[] val = Io.ReadBytesUntil(stream, (byte) '\0');
            assertArrayEquals(val, "abcdef".getBytes());
        }
    }

    @Test
    public void testBasicString() throws Exception {
        testInvarianceBasic('<', 's', "abcdef\0".getBytes(), "abcdef".getBytes());
    }

    @Test
    public void testBasicIntLe() throws Exception {
        testInvarianceBasic('<', 'i', "\2\0\0\0".getBytes(), 2);
    }

    @Test
    public void testBasicIntBe() throws Exception {
        testInvarianceBasic('>', 'i', "\0\0\0\2".getBytes(), 2);
    }

    @Test
    public void testListChar() throws Exception {
        testInvariance('<', 'h', Arrays.asList("c"), "\3\0a\0c".getBytes(), Arrays.asList('a', '\0', 'c'));
    }

    @Test
    public void testListNibble() throws Exception {
        testInvariance('<', 'h', Arrays.asList("h"), "\3\0\1\0\2\0\3\0".getBytes(), Arrays.asList((short)1, (short)2, (short)3));
    }

    @Test
    public void testListList() throws Exception {
        testInvariance('<', 'h', Arrays.asList(Arrays.asList("b")), "\2\0\2\0\0\1\2\0\2\3".getBytes(), Arrays.asList(Arrays.asList((byte)0, (byte)1), Arrays.asList((byte)2, (byte)3)));
    }

    @Test
    public void testObjectCharInt() throws Exception {
        testInvariance('<', 'h', new Tuple("c", "i"), "a\3\0\0\0".getBytes(), new Tuple('a', 3));
    }

    @Test
    public void testObjectNibbleStringChar() throws Exception {
        testInvariance('<', 'h', new Tuple("h", "s", "c"), "\2\0abcdef\0x".getBytes(), new Tuple((short)2, "abcdef".getBytes(), 'x'));
    }

    @Test
    public void testObjectObject() throws Exception {
        testInvariance('<', 'h', new Tuple(new Tuple(new Tuple("c")), new Tuple("c")), "ab".getBytes(), new Tuple(new Tuple(new Tuple('a')), new Tuple('b')));
    }

    @Test
    public void testListTuple() throws Exception {
        testInvariance('<', 'h', Arrays.asList("c", "c", "c"), "\2\0abcabc".getBytes(), Arrays.asList('a', 'b', 'c', 'a', 'b', 'c'));
    }

    @Test
    public void testListObject() throws Exception {
        testInvariance('<', 'h', Arrays.asList(new Tuple("c", "c", "c")), "\2\0abcabc".getBytes(), Arrays.asList(new Tuple('a', 'b', 'c'), new Tuple('a', 'b', 'c')));
    }

    @Test
    public void testListObjectTuple() throws Exception {
        testInvariance('<', 'h', Arrays.asList(new Tuple("c", "c"), "c"), "\2\0abcabc".getBytes(), Arrays.asList(new Tuple('a', 'b'), 'c', new Tuple('a', 'b'), 'c'));
    }
}
