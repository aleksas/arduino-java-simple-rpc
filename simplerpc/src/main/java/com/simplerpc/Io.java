package com.simplerpc;

import java.util.Arrays;

/**
 * Io class.
 */
public class Io {
    /**
     * Select the appropriate casting function given a C type.
     * @param c_type C type.
     * @return Casting function.
     */
    public static Class Cast(String c_type) {
        if (c_type == "?")
            return boolean.class;
        else {
            if (Arrays.asList("c", "s").contains(c_type))
                return byte[].class;
            if (Arrays.asList("f", "d").contains(c_type))
                return float.class;
            return int.class;
        }
    } 
}
