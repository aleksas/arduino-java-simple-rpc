package io.github.aleksas.simplerpc;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.aleksas.simplerpc.HalfPrecisionFloat;

public class HalfPrecisionFloatTest {

  private byte[] simulateBytes(final float fullPrecision) {
    HalfPrecisionFloat halfFloat = new HalfPrecisionFloat(fullPrecision);
    short halfShort = halfFloat.getHalfPrecisionAsShort();

    ByteBuffer buffer = ByteBuffer.allocate(2);
    buffer.putShort(halfShort);
    return buffer.array();
  }

  @Test
  public void testHalfPrecisionToFloatApproach() {
    final float startingValue = 1.2f;
    final float closestValue = 1.2001953f;
    final short shortRepresentation = (short) 15565;

    byte[] bytes = simulateBytes(startingValue);
    HalfPrecisionFloat halfFloat = new HalfPrecisionFloat(bytes);
    final float retFloat = halfFloat.getFullFloat();
    assertEquals(Float.valueOf(closestValue), Float.valueOf(retFloat));

    HalfPrecisionFloat otherWay = new HalfPrecisionFloat(retFloat);
    final short shrtValue = otherWay.getHalfPrecisionAsShort();
    assertEquals(Short.valueOf(shortRepresentation), Short.valueOf(shrtValue));

    HalfPrecisionFloat backAgain = new HalfPrecisionFloat(shrtValue);
    final float backFlt = backAgain.getFullFloat();
    assertEquals(Float.valueOf(closestValue), Float.valueOf(backFlt));

    HalfPrecisionFloat dbl = new HalfPrecisionFloat(startingValue);
    final double retDbl = dbl.getFullDouble();
    assertEquals(Double.valueOf(startingValue), Double.valueOf(retDbl));
  }

  @Test
  public void testInvalidByteArray() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ByteBuffer buffer = ByteBuffer.allocate(4);
      buffer.putFloat(Float.MAX_VALUE);
      byte[] bytes = buffer.array();

      new HalfPrecisionFloat(bytes);
    });
  }

  @Test
  public void testInvalidMaxFloat() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new HalfPrecisionFloat(Float.MAX_VALUE);
    });
  }

  @Test
  public void testInvalidMinFloat() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new HalfPrecisionFloat(-35000);
    });
  }

  @Test
  public void testCreateWithShort() {
    HalfPrecisionFloat sut = new HalfPrecisionFloat(Short.MAX_VALUE);
    assertEquals(Short.MAX_VALUE, sut.getHalfPrecisionAsShort());
  }
}