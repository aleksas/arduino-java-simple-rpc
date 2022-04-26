/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.github.aleksas.simplerpc;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.aleksas.simplerpc.Method;
import io.github.aleksas.simplerpc.Parameter;
import io.github.aleksas.simplerpc.Protocol;
import io.github.aleksas.simplerpc.Tuple;

public class ProtocolTest {
    @Test
    public void testParseTypeNone() {
        assertEquals(null, Protocol.ParseType(ByteBuffer.wrap("".getBytes())));
    }
    
    @Test
    public void testParseTypeBasic() {
        assertEquals("i", Protocol.ParseType(ByteBuffer.wrap("i".getBytes())));
    }
    
    @Test
    public void parseTypeTuple() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Protocol.ParseType(ByteBuffer.wrap("ic".getBytes()));
        });
    }

    @Test public void testParseTypeListBasic() {
        var list = Arrays.asList("i");
        assertEquals(list, Protocol.ParseType(ByteBuffer.wrap("[i]".getBytes())));
    }

    @Test public void testParseTypeObjectBasic() {
        var tuple = new Tuple("i");
        assertEquals(tuple, Protocol.ParseType(ByteBuffer.wrap("(i)".getBytes())));
    }

    @Test public void testParseTypeListTuple() {
        var list = Arrays.asList("i", "c");
        assertEquals(list, Protocol.ParseType(ByteBuffer.wrap("[ic]".getBytes())));
    }

    @Test public void testParseTypeListObject() {
        var tuple = new Tuple("i", "c");
        var list = Arrays.asList(tuple);
        assertEquals(list, Protocol.ParseType(ByteBuffer.wrap("[(ic)]".getBytes())));
    }

    @Test public void testParseTypeListList() {
        var list = Arrays.asList(Arrays.asList("i"));
        assertEquals(list, Protocol.ParseType(ByteBuffer.wrap("[[i])]".getBytes())));
    }

    @Test public void testParseTypeObjectTuple() {
        var pair = new Tuple("i", "c");
        assertEquals(pair, Protocol.ParseType(ByteBuffer.wrap("(ic)".getBytes())));
    }

    @Test public void testParseTypeObjectList() {
        var tuple = new Tuple(Arrays.asList("i"));
        assertEquals(tuple, Protocol.ParseType(ByteBuffer.wrap("([i])".getBytes())));
    }

    @Test public void testParseTypeObjectObject() {
        var tuple0 = new Tuple("i", "c");
        var tuple1 = new Tuple(tuple0);
        assertEquals(tuple1, Protocol.ParseType(ByteBuffer.wrap("((ic))".getBytes())));
    }

    @Test public void testParseTypeComplex() {
        var tuple0 = new Tuple(new String("c"), new String("c"));
        var tuple1 = new Tuple(tuple0, new String("c"));

        var tuple2 = new Tuple(Arrays.asList(new String("c")));

        var tuple3 = new Tuple(tuple1, new String("i"), tuple2);
        assertEquals(tuple3, Protocol.ParseType(ByteBuffer.wrap("(((cc)c)i([c]))".getBytes())));
    }

    @Test public void testParseTypeNameNone() {
        assertEquals("", Protocol.TypeName(null));
    }

    @Test public void testParseTypeNameBasic() {
        assertEquals("Byte[]", Protocol.TypeName("c"));
        assertEquals("Integer", Protocol.TypeName("i"));
        assertEquals("boolean", Protocol.TypeName("?"));
        assertEquals("Float", Protocol.TypeName("f"));
    }

    @Test public void testParseTypeNameTupleBasic() {
        assertEquals("[Integer, Byte[]]", Protocol.TypeName(Arrays.asList("i", "c")));
        assertEquals("[boolean, Float]", Protocol.TypeName(Arrays.asList("?", "f")));
    }

    @Test public void testParseTypeNameListBasic() {
        assertEquals("[[Integer]]", Protocol.TypeName(Arrays.asList(Arrays.asList("i"))));
        assertEquals("[boolean, Float]", Protocol.TypeName(Arrays.asList("?", "f")));
    }

    @Test public void testParseTypeNameObjectBasic() {
        assertEquals("[(Integer)]", Protocol.TypeName(Arrays.asList(new Tuple("i"))));
    }

    @Test public void testParseTypeNameComplex() {
        assertEquals("[((Byte[], Byte[]), Byte[]), Integer, ([Byte[]])]", 
            Protocol.TypeName(
                Arrays.asList(
                    new Tuple(new Tuple("c", "c"), "c"),
                    "i", 
                    new Tuple(Arrays.asList("c"))
                )
            )
        );
    }

    @Test public void testParseSignatureBasic() {
        var method = new Method(1, "method1");

        method.parameters.add(new Parameter("arg0", "c", "Byte[]"));
        method.parameters.add(new Parameter("arg1", "f", "Float"));
        
        assertEquals(method, Protocol.ParseSignature(1, ":c f"));
    }
    
    @Test public void testParseSignatureComplex() {
        var method = new Method(2, "method2");

        method.parameters.add(new Parameter("arg0", Arrays.asList("c"), "[Byte[]]"));
        method.parameters.add(new Parameter("arg1", new Tuple("c", "f"), "(Byte[], Float)"));
        method.ret.fmt = new Tuple("f", "f");
        method.ret.tyme_name = "(Float, Float)";
        
        assertEquals(method, Protocol.ParseSignature(2, "(ff): [c] (cf)"));
    }

    @Test public void testParseSplitTrim() {
        assertEquals(Arrays.asList("p1", "Param 1."), Arrays.asList(Protocol.SplitTrim(" p1 : Param 1. ", ":")));
        assertEquals(Arrays.asList("p1", "Param 1."), Arrays.asList(Protocol.SplitTrim("p1:Param 1.", ":")));
    }

    @Test public void testParseAddDocBasic() {
        var method = Protocol.ParseSignature(1, "i: c f");
        Protocol.AddDoc(method, ByteBuffer.wrap("name: Test. @p1: Char. @p2: Float. @return: Int.".getBytes()));

        assertEquals("name", method.name);
        assertEquals("Test.", method.doc);
        assertEquals("p1", method.parameters.get(0).name);
        assertEquals("Char.", method.parameters.get(0).doc);
        assertEquals("p2", method.parameters.get(1).name);
        assertEquals("Float.", method.parameters.get(1).doc);
        assertEquals("Int.", method.ret.doc);
    }

    @Test public void addDocMissingName() {
        var method = Protocol.ParseSignature(1, ": c f");
        Protocol.AddDoc(method, ByteBuffer.wrap("@p1: Char. @p2: Float.".getBytes()));

        assertEquals("method1", method.name);
        assertEquals("", method.doc);
        assertEquals("arg0", method.parameters.get(0).name);
    }

    @Test public void addDocMissingParameter() {
        var method = Protocol.ParseSignature(1, ": c f");
        Protocol.AddDoc(method, ByteBuffer.wrap("name: Test. @p1: Char".getBytes()));

        assertEquals("name", method.name);
        assertEquals("p1", method.parameters.get(0).name);
        assertEquals("arg1", method.parameters.get(1).name);
    }

    @Test public void testParseLine() {
        var method = Protocol.ParseLine(1, ByteBuffer.wrap("i: c f;name: Test. @p1: Char. @p2: Float. @return: Int.".getBytes()));

        assertEquals(1, method.index);
        assertEquals("name", method.name);
    }
}