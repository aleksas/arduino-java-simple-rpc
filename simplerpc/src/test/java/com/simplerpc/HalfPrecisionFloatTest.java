package com.simplerpc;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

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
    Assert.assertEquals(new Float(closestValue), new Float(retFloat));

    HalfPrecisionFloat otherWay = new HalfPrecisionFloat(retFloat);
    final short shrtValue = otherWay.getHalfPrecisionAsShort();
    Assert.assertEquals(new Short(shortRepresentation), new Short(shrtValue));

    HalfPrecisionFloat backAgain = new HalfPrecisionFloat(shrtValue);
    final float backFlt = backAgain.getFullFloat();
    Assert.assertEquals(new Float(closestValue), new Float(backFlt));

    HalfPrecisionFloat dbl = new HalfPrecisionFloat(startingValue);
    final double retDbl = dbl.getFullDouble();
    Assert.assertEquals(new Double(startingValue), new Double(retDbl));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidByteArray() {
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.putFloat(Float.MAX_VALUE);
    byte[] bytes = buffer.array();

    new HalfPrecisionFloat(bytes);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMaxFloat() {
    new HalfPrecisionFloat(Float.MAX_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMinFloat() {
    new HalfPrecisionFloat(-35000);
  }

  @Test
  public void testCreateWithShort() {
    HalfPrecisionFloat sut = new HalfPrecisionFloat(Short.MAX_VALUE);
    Assert.assertEquals(Short.MAX_VALUE, sut.getHalfPrecisionAsShort());
  }
}