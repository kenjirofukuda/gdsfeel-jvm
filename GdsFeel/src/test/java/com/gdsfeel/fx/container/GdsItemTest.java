/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 *
 * @author kenjiro
 */
public class GdsItemTest extends TestCase {

  private GdsItem item;

  public GdsItemTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    item = new GdsItem();
  }

  @Override
  protected void tearDown() throws Exception {
    item = null;
    super.tearDown();
  }

  public void testGetMag() {
    item.setMag(1.0);
    assertTrue(item.getMag() == 1.0);
    item.setMag(3.14);
    assertTrue(item.getMag() == 3.14);
  }

  public void testSetMag() {
    try {
      item.setMag(0.0);
      fail("mag can't specify < 0.0");
    }
    catch (IllegalArgumentException argumentExp) {
      argumentExp.printStackTrace();
    }

    try {
      item.setMag(-3.0);
      fail("mag can't specify < 0.0");
    }
    catch (IllegalArgumentException argumentExp) {
      argumentExp.printStackTrace();
    }
  }

  public void testMagProperty() {
    item.setMag(0.01);
    assertEquals(0.01, item.magProperty().get(), 1E-16);
  }

  public void testGetAngle() {
    for (double angle = -3600; angle < 3600; angle += 5.0) {
      item.setAngle(angle);
      assertTrue(
              "angle >= 0.0 && angle < 360",
              item.getAngle() >= 0.0 && item.getAngle() < 360.0);
    }
  }

  public void testSetAngle() {
    item.setAngle(30.0);
    assertEquals(30.0, item.getAngle(), 1e-8);

    item.setAngle(-30.0);
    assertEquals(330.0, item.getAngle(), 1e-8);

    item.setAngle(370.0);
    assertEquals(10.0, item.getAngle(), 1e-8);
  }

  public void testAngleProperty() {
    item.setAngle(-10);
    assertEquals(350, item.angleProperty().get(), 1E-16);
    item.angleProperty().set(370);
    assertEquals(10, item.angleProperty().get(), 1E-16);
  }

  public void testIsReflected() {
    GdsItem newItem = new GdsItem();
    assertFalse(newItem.isReflected());
    newItem.reflectedProperty().set(true);
    assertTrue(newItem.isReflected());
  }

  public void testReflectedProperty() {
    SimpleBooleanProperty testBoolean = new SimpleBooleanProperty();
    testBoolean.addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
        System.out.println("ov = " + ov);
        System.out.println("t = " + t);
        System.out.println("t1 = " + t1);
      }
    });

    testBoolean.bind(item.reflectedProperty());
    item.reflectedProperty().set(true);
    assertTrue(testBoolean.get());
    item.reflectedProperty().set(false);
    assertFalse(testBoolean.get());
    item.reflectedProperty().set(true);
    assertTrue(testBoolean.get());

    testBoolean.unbind();
    testBoolean.bindBidirectional(item.reflectedProperty());
    item.reflectedProperty().set(false);
    assertFalse(testBoolean.get());
    testBoolean.set(true);
    assertTrue(item.reflectedProperty().get());
  }

  public void testSetReflected() {
    //fail("test not found");
  }

  public void testGetCoords() {
    GdsItem newItem = new GdsItem();
    assertTrue(newItem.coordsProperty().isEmpty());

  }

  public void testSetCoords() {
    //fail("test not found");
  }
}
