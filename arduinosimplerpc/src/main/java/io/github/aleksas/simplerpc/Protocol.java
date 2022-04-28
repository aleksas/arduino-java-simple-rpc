package io.github.aleksas.simplerpc;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple RPC protocol class.
 */
class Protocol {
    /**
     * Java type name of a C object type.
     * @param type C object type.
     * @return Java type name.
     */
    public static String TypeName(Object obj_type) {
        var types = new ArrayList<String>();
        if (obj_type == null) {
            return "";
        } else if (obj_type instanceof Tuple) {
            var tuple = (Tuple) obj_type;
            for (var object: tuple.toList())
                types.add(TypeName(object));
            return "(" + String.join(", ", types) + ")";            
        } else if (obj_type instanceof List){
            var list = (List) obj_type;
            for (var object: list)
                types.add(TypeName(object));
            return "[" + String.join(", ", types) + "]";       
        }
        if (obj_type instanceof Object[]) {
            return TypeName(Arrays.asList((Object[]) obj_type));            
        }

        return Io.TypeClass(((String) obj_type).charAt(0)).getSimpleName();
    }

    public static Object ConstructType(ByteBuffer tokens) {
        var object_type = new ArrayList<Object>();

        while(tokens.hasRemaining()) {
            var token = tokens.get();

            if (token == (byte)'[') {
                object_type.add(ConstructType(tokens));
            } else if (token == (byte)'(') {
                var subtype = ConstructType(tokens);
                if (subtype instanceof ArrayList) {
                    var tuple = new Tuple(((ArrayList) subtype).toArray());
                    object_type.add(tuple);
                } else {
                    return new Tuple(subtype);
                }
            } else if (token == (byte)')' || token == (byte)']') {
                break;
            } else {
                object_type.add(new String(new byte[] {token}));
            }   
        }
        
        return object_type;
    }

    /**
     * Parse a type definition string.
     * @param type_str Type definition string.
     * @return Type object.
     */
    public static Object ParseType(ByteBuffer bytes) {
        var obj_type = ConstructType(bytes);

        int size = 0;
        if (obj_type instanceof Tuple) {
            var tuple = (Tuple) obj_type;
            size = tuple.getSize();
            if (size == 1) {
                return tuple.getValue(0);
            }
        } else if (obj_type instanceof List) {
            var list = (List) obj_type;
            size = list.size();
            if (size == 1) {
                return list.get(0);
            }
        } else {
            throw new RuntimeException();
        }

        if (size == 0) {
            return null;
        } else if (size > 1) {
            throw new RuntimeException("top level type can not be tuple");
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * Parse a C function signature string.
     * @param index Function index.
     * @param signature Function signature.
     * @return Method object.
     */
    public static Method ParseSignature(int index, String signature) {
        return ParseSignature(index, signature.getBytes());
    }

    /**
     * Parse a C function signature string.
     * @param index Function index.
     * @param signature Function signature.
     * @return Method object.
     */
    public static Method ParseSignature(int index, byte[] signature) {
        return ParseSignature(index, ByteBuffer.wrap(signature));
    }

    /**
     * Parse a C function signature string.
     * @param index Function index.
     * @param signature Function signature.
     * @return Method object.
     */
    public static Method ParseSignature(int index, ByteBuffer signature) {
        var method = new Method(index, String.format("method%d", index)) ;

        var parameters = signature.slice();
        while(parameters.get() != (byte) ':') {}

        var fmt = signature.slice(0, parameters.position() - 1);
        var parsed_type = ParseType(fmt);

        method.ret.fmt = parsed_type;
        method.ret.tyme_name = TypeName(parsed_type);
        
        int param_index = 0;
        for (var parameter_fmt : Utils.Split(parameters, (byte) ' ')) {
            var parameter_type = ParseType(parameter_fmt);

            method.parameters.add(
                new Parameter(
                    String.format("arg%d", param_index),
                    parameter_type,
                    TypeName(parameter_type)
                )
            );
            param_index++;
        }

        return method;
    }

    static String[] SplitTrim(String string, String delimiter) {
        return string.trim().split("\\s*" + delimiter + "\\s*");
    }

    /**
     * Add documentation to a method object.
     * @param method Method object.
     * @param doc Method documentation.
     */
    static void AddDoc(Method method , ByteBuffer doc) {
        var bytes = new byte[doc.remaining()];
        doc.get(bytes);

        var splits = new String(bytes, StandardCharsets.UTF_8).split("@");
        var parts = new ArrayList<String[]>();
        
        for (var split : splits) {
            var tmp = SplitTrim(split, ":");
            parts.add(tmp);
            if (tmp.length != 2) {
                return;
            }
        }

        int index = 0;
        boolean first = true;
        for (var part : parts) {
            var name = part[0];
            var description = part[1];

            if(first) {
                method.name = name;
                method.doc = description;
                first = false;
            } else {
                if (!name.equals("return")) {
                    if (index < method.parameters.size()) {
                        method.parameters.get(index).name = name;
                        method.parameters.get(index).doc = description;
                    }
                    index++;
                } else {
                    method.ret.doc = description;
                }
            }
        }
    }
    
    /**
     * Parse a method definition line.
     * @param index Line number.
     * @param bytes Method definition.
     * @return Method object.
     */
    public static Method ParseLine(int index, ByteBuffer buffer) {
        var splits = Utils.Split(buffer, (byte) ';').iterator();
        var signature = splits.next();
        var description = splits.next();

        var method = ParseSignature(index, signature);
        AddDoc(method, description);

        return method;
    }
}
