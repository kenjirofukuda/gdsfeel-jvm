/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kenjiro
 */
public class GdsPointTest {

  public GdsPointTest() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void print() {
    System.out.println(new GdsPoint(0, 0));
    System.out.println(new GdsPoint(3, 4));
    System.out.println(new GdsPoint(8, 8));
  }
}
