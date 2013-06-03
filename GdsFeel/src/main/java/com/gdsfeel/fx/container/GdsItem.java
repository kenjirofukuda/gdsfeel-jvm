/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.container;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.apache.commons.lang.Validate;

/**
 *
 * @author kenjiro
 */
public class GdsItem {

  private GdsMutablePoints coords = new GdsMutablePoints(this, "coords");
  private DoubleProperty mag = new SimpleDoubleProperty(this, "mag", 1.0);
  private DoubleProperty angle = new SimpleDoubleProperty(this, "angle", 0.0);
  private BooleanProperty reflected = new SimpleBooleanProperty(this, "reflected", false);

  public GdsItem() {
    angle.addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
        angle.set(settingValue(t1.doubleValue()));
        if (t != null && t1 != null) {
          if (t.doubleValue() == settingValue(t1.doubleValue())) {
            //angle
          }
        }
      }
    });
    angle.addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable o) {
        System.out.println("invalidate =" + o);
      }
    });
  }

  public GdsMutablePoints coordsProperty() {
    return coords;
  }

  /**
   * Get the value of reference structure magnify
   *
   * @return the value of magnify
   */
  public double getMag() {
    return mag.get();
  }

  /**
   * Set the value of reference structure magnify
   *
   * @param mag if <= 0.0 IlligalArgumentException
   */
  public void setMag(double mag) {
    Validate.isTrue(mag > 0.0, "mag can't spscify <= 0.0", mag);
    this.mag.set(mag);
  }

  public DoubleProperty magProperty() {
    return mag;
  }

  /**
   *
   * @return 0.0 to 360.0 degree value
   */
  public double getAngle() {
    return angle.get();
  }

  /**
   * set reference structure rotation degree angle
   *
   * set: -10 inside: 350 set: 370 inside: 10
   *
   * @param angle counter clock wise degree value
   */
  public void setAngle(double angle) {
    this.angle.set(angle);
//    this.angle.set(settingValue(angle));
  }

  private double settingValue(double angle) {
    double absValue = Math.abs(angle);
    double rest = absValue % 360.0;
    double settingValue = rest;
    if (rest != 0.0) {
      settingValue = angle > 0.0 ? rest : (360.0 - rest);
    }
    Validate.isTrue(settingValue >= 0.0 && settingValue < 360.0,
            "settingValue = " + settingValue);
    return settingValue;
  }

  public DoubleProperty angleProperty() {
    return angle;
  }

  public boolean isReflected() {
    return reflected.get();
  }

  public void setReflected(boolean reflected) {
    this.reflected.set(reflected);
  }

  public final BooleanProperty reflectedProperty() {
    return reflected;
  }

  public boolean isHavePoints() {
    return !coords.isEmpty();
  }
}
