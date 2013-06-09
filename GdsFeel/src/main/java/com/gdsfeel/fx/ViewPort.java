/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx;

import com.gdsfeel.Structure;
import com.gdsfeel.fx.container.GdsPoint;
import com.gdsfeel.util.Conv;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Rectangle2DBuilder;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FX Version
 * @author kenjiro
 */
public class ViewPort {

  private static Log log = LogFactory.getLog(ViewPort.class);
  private Structure structure;
  private ArrayList<AffineTransform> transformStack;
  private DoubleProperty portWidth;
  private DoubleProperty portHeight;
  private DoubleProperty portCenterX;
  private DoubleProperty portCenterY;
  private DoubleProperty centerX;
  private DoubleProperty centerY;
  private DoubleProperty scale;
  private ObjectProperty<AffineTransform> transform;
  private ObjectProperty<AffineTransform> basicTransform;

  public ViewPort(Structure structure) {
    this.structure = structure;
    transformStack = new ArrayList<>();
    portWidth = new SimpleDoubleProperty(this, "portWidth", 0.0);
    portHeight = new SimpleDoubleProperty(this, "portHeight", 0.0);
    portCenterX = new SimpleDoubleProperty(this, "portCenterX", 0.0);
    portCenterY = new SimpleDoubleProperty(this, "portCenterY", 0.0);
    centerX = new SimpleDoubleProperty(this, "centerX", 0.0);
    centerY = new SimpleDoubleProperty(this, "centerY", 0.0);
    scale = new SimpleDoubleProperty(this, "scale", 1.0);
    transform = new SimpleObjectProperty(this, "transform");
    basicTransform = new SimpleObjectProperty(this, "basicTransform");

    installDamageHandler(portWidth);
    installDamageHandler(portHeight);
    installDamageHandler(portCenterX);
    installDamageHandler(portCenterY);
    installDamageHandler(centerX);
    installDamageHandler(centerY);
    installDamageHandler(scale);

    installPortSizeChangeHandler(portWidth);
    installPortSizeChangeHandler(portHeight);
  }

  public DoubleProperty portWidthProperty() {
    return portWidth;
  }

  public DoubleProperty portHeightProperty() {
    return portHeight;
  }

  private void installDamageHandler(DoubleProperty dp) {
    dp.addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
        log.debug("ov = " + ov);
        log.debug("oldValue = " + t);
        log.debug("newValue = " + t);
        damageTransform();
      }
    });
  }

  private void installPortSizeChangeHandler(DoubleProperty dp) {
    dp.addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
        log.debug("ov = " + ov);
        log.debug("oldValue = " + t);
        log.debug("newValue = " + t);
        resetPortCenter();
      }
    });
  }

  public void setPortSize(double w, double h) {
    Validate.isTrue(w > 0.0);
    Validate.isTrue(h > 0.0);
    portWidth.set(w);
    portHeight.set(h);
  }

  public void setPortCenter(double x, double y) {
    portCenterX.set(x);
    portCenterY.set(portHeight.get() - y);
  }

  public void resetPortCenter() {
    setPortCenter(portWidth.get() / 2.0, portHeight.get() / 2.0);
  }

  public void setCenter(double x, double y) {
    centerX.set(x);
    centerY.set(y);
  }

  public GdsPoint getCenter() {
    return GdsPoint.xy(centerX.get(), centerY.get());
  }

  public void setScale(double newScale) {
    Validate.isTrue(newScale != 0.0, "scale disallow 0.0;");
    if (scale.get() == newScale) {
      return;
    }
    if (newScale <= 0.001) {
      log.error(String.format("newScale is %f convert to safe margin value ==> 0.001", newScale));
      newScale = 0.001;
    }
    scale.set(newScale);
  }

  public double getScale() {
    return scale.get();
  }

  public void zoom(double ratio) {
    setScale(getScale() * ratio);
  }

  public void setBounds(Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    double hRatio = portWidth.get() / newBounds.getWidth();
    double vRatio = portHeight.get() / newBounds.getHeight();
    double newScale = hRatio < vRatio ? hRatio : vRatio;
    centerX.set(Conv.centerX(newBounds));
    centerY.set(Conv.centerY(newBounds));
    scale.set(newScale);
  }

  public Rectangle2D getBounds() {
    Rectangle2D deviceBounds = new Rectangle2D(0, 0, portWidth.get(), portHeight.get());
    Rectangle2D worldBounds = Rectangle2D.EMPTY;

    AffineTransform inverseTx;
    try {
      inverseTx = getTransform().createInverse();
      java.awt.geom.Point2D min = inverseTx.transform(
              new java.awt.geom.Point2D.Double(deviceBounds.getMinX(), deviceBounds.getMinY()), null);
      java.awt.geom.Point2D max = inverseTx.transform(
              new java.awt.geom.Point2D.Double(deviceBounds.getMaxX(), deviceBounds.getMaxY()), null);
      worldBounds = Rectangle2DBuilder.create().minX(min.getX()).minY(min.getY())
              .width(max.getX() - min.getX()).height(max.getY() - min.getY()).build();
    }
    catch (NoninvertibleTransformException ex) {
      log.error(ex);
    }
    return worldBounds;
  }

  public AffineTransform getBasicTransform() {
    if (basicTransform.get() == null) {
      basicTransform.set(lookupBasicTransform());
    }
    return basicTransform.get();
  }

  public AffineTransform getTransform() {
    if (transform.get() == null) {
      AffineTransform newTransform = new AffineTransform();
      newTransform.concatenate(getBasicTransform());
      for (AffineTransform tx : transformStack) {
        newTransform.concatenate(tx);
      }
      transform.set(newTransform);
    }
    return transform.get();
  }

  public void pushTransform(AffineTransform newTransform) {
    Validate.notNull(newTransform);
    transformStack.add(newTransform);
    transform.set(null);
  }

  public AffineTransform popTransform() {
    if (transformStack.isEmpty()) {
      return null;
    }
    AffineTransform result;
    result = transformStack.remove(transformStack.size() - 1);
    transform.set(null);
    return result;
  }

  public void fit() {
    setBounds(structure.getBoundingBox2());
  }

  public void viewMoveFraction(double x, double y) {
    Rectangle2D viewBounds = getBounds();
    double deltaX = viewBounds.getWidth() * x;
    double deltaY = viewBounds.getHeight() * y;
    GdsPoint newCenter = getCenter();
    setCenter(newCenter.getX() + deltaX, newCenter.getY() + deltaY);
  }

  private void damageTransform() {
    transform.set(null);
    basicTransform.set(null);
  }

  private AffineTransform lookupBasicTransform() {
    AffineTransform tx = new AffineTransform();
    if (structure.isEmpty()) {
      return tx;
    }
    tx.concatenate(flipTransform());
    tx.concatenate(toCenterTransform());
    double s = scale.get() * getVisibleRatio();
    tx.concatenate(AffineTransform.getScaleInstance(s, s));
    tx.concatenate(viewToCenterTransform());
    return tx;
  }

  private AffineTransform flipTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, portHeight.get());
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform toCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(portCenterX.get(), portCenterY.get());
    return tx;
  }

  private AffineTransform viewToCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(-centerX.get(), -centerY.get());
    return tx;
  }

  private Rectangle2D modelBounds() {
    return structure.getBoundingBox2();
  }

  public double getVisibleRatio() {
    // TODO: add setter and more ...
    return 0.98;
  }
}
