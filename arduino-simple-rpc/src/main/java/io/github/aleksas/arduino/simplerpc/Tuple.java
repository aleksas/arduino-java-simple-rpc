package io.github.aleksas.arduino.simplerpc;

/**
 * Var size tuple.
 */
public class Tuple extends org.javatuples.Tuple {
    public Tuple(final Object... values) {
        super(values);
    }    

    @Override
    public int getSize() {
        return toList().size();
    }
}
