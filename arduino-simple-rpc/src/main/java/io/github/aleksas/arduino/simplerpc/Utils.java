package io.github.aleksas.arduino.simplerpc;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Helper functions.
 */
public class Utils {
    /**
     * Split ByteBuffer by separator byte.
     * @param buffer Byte buffer.
     * @param separator Separator byte.
     * @return Iterator of byte buffers split by separatir byte. 
     */
    public static Iterable<ByteBuffer> Split(ByteBuffer buffer, byte separator) {        
        return new Iterable<ByteBuffer>() {            
            ByteBuffer slice = buffer.slice();
            int start_index = 0;

            @Override
            public Iterator<ByteBuffer> iterator() {
                return new Iterator<ByteBuffer>() {    
                    @Override
                    public boolean hasNext() {
                        ByteBuffer tmp = slice.slice();
                        while(tmp.hasRemaining()) {
                            if (tmp.get() != separator) {
                                return true;
                            }
                        }
                        return false;
                    }
    
                    @Override
                    public ByteBuffer next() {
                        boolean collecting = false;
                        while(slice.hasRemaining()) {
                            byte value = slice.get();
                
                            if (collecting) {
                                if (value == separator) {
                                    int tmpPosition = slice.position();
                                    slice.position(start_index);
                                    ByteBuffer ret = slice.slice();
                                    ret.limit(tmpPosition - start_index - 1);
                                    slice = slice.position(tmpPosition);
                                    return ret;
                                }
                            } else {
                                if (value != separator) {
                                    collecting = true;
                                    start_index = slice.position() - 1;
                                }
                            }   
                            
                            if (value == separator) {
                                start_index = slice.position();
                            }
                        }

                        if (collecting) {
                            int tmpPosition = slice.position();
                            slice.position(start_index);
                            ByteBuffer ret = slice.slice();
                            slice.position(tmpPosition);
                            return ret;
                        } else {
                            throw new RuntimeException();
                        }
                    }    
                };
            }
        };
    }
}
