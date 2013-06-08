/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import com.gdsfeel.fx.container.GdsPoint;
import com.gdsfeel.util.Conv;
import java.awt.Dimension;
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
 *
 * @author kenjiro
 */
public class ViewPort {

  private static Log log = LogFactory.getLog(ViewPort.class);
  private Structure _structure;
  private ArrayList<AffineTransform> _transformStack;
  private Dimension _portSize;
  private java.awt.geom.Point2D _center;
  private double _scale;
  private AffineTransform _transform;
  private AffineTransform _basicTransform;
  private DoubleProperty portWidth;
  private DoubleProperty portHeight;
  private DoubleProperty centerX;
  private DoubleProperty centerY;
  private DoubleProperty scale;
  private ObjectProperty<AffineTransform> transform;
  private ObjectProperty<AffineTransform> basicTransform;

  public ViewPort(Structure structure) {
    _structure = structure;
    _transformStack = new ArrayList<>();
    _portSize = new Dimension(0, 0);
    _scale = 1.0;
    _center = new java.awt.geom.Point2D.Double(0, 0);
    portWidth = new SimpleDoubleProperty(this, "portWidth", 0.0);
    portHeight = new SimpleDoubleProperty(this, "portHeight", 0.0);
    centerX = new SimpleDoubleProperty(this, "centerX", 0.0);
    centerY = new SimpleDoubleProperty(this, "centerY", 0.0);
    scale = new SimpleDoubleProperty(this, "scale", 1.0);
    transform = new SimpleObjectProperty(this, "transform");
    basicTransform = new SimpleObjectProperty(this, "basicTransform");

    installDamageHandler(portWidth);
    installDamageHandler(portHeight);
    installDamageHandler(centerX);
    installDamageHandler(centerY);
    installDamageHandler(scale);
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
        damageTransform2();
      }
    });
  }

  public void setPortSize(Dimension newSize) {
    Validate.notNull(newSize);
    Validate.isTrue(newSize.getWidth() != 0);
    Validate.isTrue(newSize.getHeight() != 0);
    if (_portSize.equals(newSize)) {
      return;
    }
    _portSize.setSize(newSize);
    damageTransform();
  }

  public void setPortSize2(double w, double h) {
    Validate.isTrue(w > 0.0);
    Validate.isTrue(h > 0.0);
    portWidth.set(w);
    portHeight.set(h);
  }

  public void setCenter(double x, double y) {
    setCenter(new java.awt.geom.Point2D.Double(x, y));
  }

  public void setCenter2(double x, double y) {
    centerX.set(x);
    centerY.set(y);
  }

  public void setCenter(java.awt.geom.Point2D newCenter) {
    Validate.notNull(newCenter);
    setCenter(newCenter.getX(), newCenter.getY());
    if (_center.equals(newCenter)) {
      return;
    }
    _center.setLocation(newCenter);
    damageTransform();
  }

  public java.awt.geom.Point2D getCenter() {
    return new java.awt.geom.Point2D.Double(_center.getX(), _center.getY());
  }

  public GdsPoint getCenter2() {
    return GdsPoint.xy(centerX.get(), centerY.get());
  }

  public void setScale(double newScale) {
    Validate.isTrue(newScale != 0.0, "scale disallow 0.0;");
    if (_scale == newScale) {
      return;
    }
    _scale = newScale;
    damageTransform();
  }

  public void setScale2(double newScale) {
    Validate.isTrue(newScale != 0.0, "scale disallow 0.0;");
    scale.set(newScale);
  }

  public void setBounds(java.awt.geom.Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    double hRatio = (double) _portSize.getWidth() / newBounds.getWidth();
    double vRatio = (double) _portSize.getHeight() / newBounds.getHeight();
    double newScale = hRatio < vRatio ? hRatio : vRatio;
    java.awt.geom.Point2D newCenter = new java.awt.geom.Point2D.Double(newBounds.getCenterX(),
                                                                       newBounds.getCenterY());
    if (newScale != _scale || (!newCenter.equals(_center))) {
      _center = newCenter;
      Validate.isTrue(newScale != 0.0, "scale disallow 0.0; ");
      _scale = newScale;
      damageTransform();
    }
  }

  public void setBounds2(Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    double hRatio = portWidth.get() / newBounds.getWidth();
    double vRatio = portHeight.get() / newBounds.getHeight();
    double newScale = hRatio < vRatio ? hRatio : vRatio;
    centerX.set(Conv.centerX(newBounds));
    centerY.set(Conv.centerY(newBounds));
    scale.set(newScale);
  }

  public java.awt.geom.Rectangle2D getBounds() {
    java.awt.geom.Rectangle2D deviceBounds =
            new java.awt.geom.Rectangle2D.Double(0, 0, _portSize.getWidth(), _portSize.getHeight());
    java.awt.geom.Rectangle2D worldBounds = new java.awt.geom.Rectangle2D.Double();
    try {
      AffineTransform inverseTx = getTransform().createInverse();
      java.awt.geom.Point2D min = inverseTx.transform(
              new java.awt.geom.Point2D.Double(deviceBounds.getMinX(), deviceBounds.getMinY()), null);
      java.awt.geom.Point2D max = inverseTx.transform(
              new java.awt.geom.Point2D.Double(deviceBounds.getMaxX(), deviceBounds.getMaxY()), null);
      worldBounds.setFrameFromDiagonal(min, max);
    }
    catch (NoninvertibleTransformException ex) {
      log.error(ex);
    }
    return worldBounds;
  }

  public Rectangle2D getBounds2() {
    Rectangle2D deviceBounds = new Rectangle2D(0, 0, portWidth.get(), portHeight.get());
    Rectangle2D worldBounds = Rectangle2D.EMPTY;

    AffineTransform inverseTx;
    try {
      inverseTx = getTransform2().createInverse();
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
    if (_basicTransform == null) {
      _basicTransform = lookupBasicTransform();
    }
    return _basicTransform;
  }

  public AffineTransform getBasicTransform2() {
    if (basicTransform.get() == null) {
      basicTransform.set(lookupBasicTransform2());
    }
    return basicTransform.get();
  }

  public AffineTransform getTransform() {
    if (_transform == null) {
      AffineTransform newTransform = new AffineTransform();
      newTransform.concatenate(getBasicTransform());
      for (AffineTransform tx : _transformStack) {
        newTransform.concatenate(tx);
      }
      _transform = newTransform;
    }
    return _transform;
  }

  public AffineTransform getTransform2() {
    if (transform.get() == null) {
      AffineTransform newTransform = new AffineTransform();
      newTransform.concatenate(getBasicTransform2());
      for (AffineTransform tx : _transformStack) {
        newTransform.concatenate(tx);
      }
      transform.set(newTransform);
    }
    return transform.get();
  }

  public void pushTransform(AffineTransform newTransform) {
    Validate.notNull(newTransform);
    _transformStack.add(newTransform);
    _transform = null;
    transform.set(null);
  }

  public AffineTransform popTransform() {
    if (_transformStack.isEmpty()) {
      return null;
    }
    AffineTransform result;
    result = _transformStack.remove(_transformStack.size() - 1);
    _transform = null;
    transform.set(null);
    return result;
  }

  public void fit() {
    setBounds(_structure.getBoundingBox());
  }

  public void fit2() {
    setBounds2(_structure.getBoundingBox2());
  }

  public void viewMoveFraction(double x, double y) {
    java.awt.geom.Rectangle2D viewBounds = getBounds();
    double deltaX = viewBounds.getWidth() * x;
    double deltaY = viewBounds.getHeight() * y;
    java.awt.geom.Point2D newCenter = getCenter();
    setCenter(newCenter.getX() + deltaX, newCenter.getY() + deltaY);
  }

  public void viewMoveFraction2(double x, double y) {
    Rectangle2D viewBounds = getBounds2();
    double deltaX = viewBounds.getWidth() * x;
    double deltaY = viewBounds.getHeight() * y;
    GdsPoint newCenter = getCenter2();
    setCenter2(newCenter.getX() + deltaX, newCenter.getY() + deltaY);
  }

  private void damageTransform() {
    _transform = null;
    _basicTransform = null;
  }

  private void damageTransform2() {
    transform.set(null);
    basicTransform.set(null);
  }

  private AffineTransform lookupBasicTransform() {
    AffineTransform tx = new AffineTransform();
    if (_structure.isEmpty()) {
      return tx;
    }
    tx.concatenate(flipTransform());
    tx.concatenate(toCenterTransform());
    tx.concatenate(AffineTransform.getScaleInstance(
            _scale * getVisibleRatio(), _scale * getVisibleRatio()));
    tx.concatenate(viewToCenterTransform());
    return tx;
  }

  private AffineTransform lookupBasicTransform2() {
    AffineTransform tx = new AffineTransform();
    if (_structure.isEmpty()) {
      return tx;
    }
    tx.concatenate(flipTransform2());
    tx.concatenate(toCenterTransform2());
    double s = scale.get() * getVisibleRatio();
    tx.concatenate(AffineTransform.getScaleInstance(s, s));
    tx.concatenate(viewToCenterTransform2());
    return tx;
  }

  private AffineTransform flipTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, _portSize.getHeight());
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform flipTransform2() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, portHeight.get());
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform toCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate((_portSize.getWidth() / 2), (_portSize.getHeight() / 2));
    return tx;
  }

  private AffineTransform toCenterTransform2() {
    AffineTransform tx = new AffineTransform();
    tx.translate((portWidth.get() / 2.0), (portHeight.get() / 2.0));
    return tx;
  }

  private AffineTransform viewToCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(-modelBounds().getCenterX(), -modelBounds().getCenterY());
    return tx;
  }

  private AffineTransform viewToCenterTransform2() {
    AffineTransform tx = new AffineTransform();
    tx.translate(
            -Conv.centerX(modelBounds2()),
            -Conv.centerY(modelBounds2()));
    return tx;
  }

  private java.awt.geom.Rectangle2D modelBounds() {
    return _structure.getBoundingBox();
  }

  private Rectangle2D modelBounds2() {
    return _structure.getBoundingBox2();
  }

  public double getVisibleRatio() {
    // TODO: add setter and more ...
    return 0.98;
  }
}
// vim: ts=2 sw=2 expandtab

