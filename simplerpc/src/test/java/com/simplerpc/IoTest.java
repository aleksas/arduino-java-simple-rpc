package com.simplerpc;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

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
            assertArrayEquals(stream.toByteArray(), data);
        }
    }

    private void testInvariance(char endianness, char size_t, Object object_def, byte[] data, Object object) throws Exception{
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            var obj = Io.Read(stream, endianness, size_t, object_def);

            assertEquals(obj, object);
        }

        // try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
        //     Io.Write(stream, endianness, basic_type, value);
        //     var d = stream.toByteArray();
        //     assertArrayEquals(stream.toByteArray(), data);
        // }
    }

    @Test
    public void testReadBytesUntil() throws Exception {
        try (ByteArrayInputStream stream = new ByteArrayInputStream("abcdef\0abc".getBytes())) {
            var val = Io.ReadBytesUntil(stream, (byte) '\0');
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
        testInvariance('<', 'h', Arrays.asList("c"), "\3\0a\0c".getBytes(), Arrays.asList((byte)'a', (byte)'\0', (byte)'c'));
    }


// def test_list_char() -> None:
// _test_invariance(
//     read, write, '<', 'h', ['c'], b'\3\0a\0c', [b'a', b'\0', b'c'])


// def test_list_nibble() -> None:
// _test_invariance(
//     read, write, '<', 'h', ['h'], b'\3\0\1\0\2\0\3\0', [1, 2, 3])


// def test_list_list() -> None:
// _test_invariance(
//     read, write, '<', 'h', [['b']], b'\2\0\2\0\0\1\2\0\2\3',
//     [[0, 1], [2, 3]])


// def test_object_char_int() -> None:
// _test_invariance(
//     read, write, '<', 'h', ('c', 'i'), b'a\3\0\0\0', (b'a', 3))


// def test_object_nibble_string_char() -> None:
// _test_invariance(
//     read, write, '<', 'h', ('h', 's', 'c'), b'\2\0abcdef\0x',
//     (2, b'abcdef', b'x'))


// def test_object_object() -> None:
// _test_invariance(
//     read, write, '<', 'h', ((('c', ), ), ('c', ), ), b'ab',
//     (((b'a', ), ), (b'b', )))


// def test_list_tuple() -> None:
// _test_invariance(
//     read, write, '<', 'h', ['c', 'c', 'c'], b'\2\0abcabc',
//     [b'a', b'b', b'c', b'a', b'b', b'c'])


// def test_list_object() -> None:
// _test_invariance(
//     read, write, '<', 'h', [('c', 'c', 'c')], b'\2\0abcabc',
//     [(b'a', b'b', b'c'), (b'a', b'b', b'c')])


// def test_list_object_tuple() -> None:
// _test_invariance(
//     read, write, '<', 'h', [('c', 'c'), 'c'], b'\2\0abcabc',
//     [(b'a', b'b'), b'c', (b'a', b'b'), b'c'])

}
