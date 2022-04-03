package com.simplerpc;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class IoTest {
    private void testInvarianceBasic(char endianness, char basic_type, byte[] data, Object value) throws Exception{
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            var val = Io.ReadBasic(stream, endianness, basic_type);

            if (val instanceof byte[] && value instanceof byte[]) {
                assertArrayEquals((byte[])val, (byte[])value);
            } else {
                assertEquals(val, value);
            }
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Io.WriteBasic(stream, endianness, basic_type, value);
            var d = stream.toByteArray();
            assertArrayEquals(stream.toByteArray(), data);
        }
    }

    @Test
    public void testBasicString() throws Exception {
        testInvarianceBasic('<', 's', "abcdef\0".getBytes(), "abcdef");
    }

    // @Test
    // public void testBasicIntLe() throws Exception {
    //     testInvarianceBasic('<', 'i', "\2\0\0\0".getBytes(), 2);
    // }

    // @Test
    // public void testBasicIntBe() throws Exception {
    //     testInvarianceBasic('>', 'i', "\0\0\0\2".getBytes(), 2);
    // }
}
