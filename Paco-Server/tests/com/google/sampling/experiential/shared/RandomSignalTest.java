// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.sampling.experiential.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

/**
 * @author corycornelius@google.com (Cory Cornelius)
 *
 */
public class RandomSignalTest {
  @Test
  public void testEquality() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    assertTrue(signal1.equals(signal2));
  }

  @Test
  public void testInequality() {
    RandomSignal signal1 = new RandomSignal();
    FixedSignal signal2 = new FixedSignal();

    assertFalse(signal1.equals(signal2));
  }

  @Test
  public void testEqualityWhenStartTimeSet() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setStartTime(new Date(3));
    signal2.setStartTime(new Date(3));

    assertTrue(signal1.equals(signal2));
  }

  @Test
  public void testEqualityWhenStartTimeSetNull() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setStartTime(null);
    signal2.setStartTime(null);

    assertTrue(signal1.equals(signal2));
  }

  @Test
  public void testInequalityWhenStartTimeSetNull() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setStartTime(new Date(3));
    signal2.setStartTime(null);

    assertFalse(signal1.equals(signal2));
  }

  @Test
  public void testEqualityWhenEndTimeSet() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setEndTime(new Date(3));
    signal2.setEndTime(new Date(3));

    assertTrue(signal1.equals(signal2));
  }

  @Test
  public void testEqualityWhenEndTimeSetNull() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setEndTime(null);
    signal2.setEndTime(null);

    assertTrue(signal1.equals(signal2));
  }

  @Test
  public void testInequalityWhenEndTimeSetNull() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setEndTime(new Date(3));
    signal2.setEndTime(null);

    assertFalse(signal1.equals(signal2));
  }

  @Test
  public void testEqualityWhenFrequencySet() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setFrequency(3);
    signal2.setFrequency(3);

    assertTrue(signal1.equals(signal2));
  }

  @Test
  public void testInequalityWhenFrequencySet() {
    RandomSignal signal1 = new RandomSignal();
    RandomSignal signal2 = new RandomSignal();

    signal1.setFrequency(3);
    signal2.setFrequency(4);

    assertFalse(signal1.equals(signal2));
  }
}