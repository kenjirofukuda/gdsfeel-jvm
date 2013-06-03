/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import javafx.collections.ListChangeListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kenjiro
 */
public class GdsPointsTest {

  public GdsPointsTest() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testAdd() {
    final GdsPoints buff = new GdsPoints();
    buff.addListener(new ListChangeListener() {
      @Override
      public void onChanged(ListChangeListener.Change change) {
        //Assert.assertTrue(change.wasAdded());
        //Assert.assertEquals(20, change.getAddedSize());
        System.out.println(buff.getBounds());
      }
    });
    GdsRandomPoint random = new GdsRandomPoint(-100, 100, -200, 200);
    random.setRoundUnit(0.125);
    buff.addAll(random.values(20));
    StringBuffer sb;
    sb = new StringBuffer();
    buff.printOn(sb);
    System.out.println(sb);

    buff.add(random.next());
    sb = new StringBuffer();
    buff.printOn(sb);
    System.out.println(sb);

    buff.remove(0, 10);
    sb = new StringBuffer();
    buff.printOn(sb);
    System.out.println(sb);
  }
}
