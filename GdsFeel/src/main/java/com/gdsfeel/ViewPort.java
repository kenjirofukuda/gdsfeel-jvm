/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  private Point2D _center;
  private double _scale;
  private AffineTransform _transform;
  private AffineTransform _basicTransform;

  public ViewPort(Structure structure) {
    _structure = structure;
    _transformStack = new ArrayList<>();
    _portSize = new Dimension(0, 0);
    _scale = 1.0;
    _center = new Point2D.Double(0, 0);
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

  public void setCenter(double x, double y) {
    setCenter(new Point2D.Double(x, y));
  }

  public void setCenter(Point2D newCenter) {
    Validate.notNull(newCenter);
    if (_center.equals(newCenter)) {
      return;
    }
    _center.setLocation(newCenter);
    damageTransform();
  }

  public Point2D getCenter() {
    return new Point2D.Double(_center.getX(), _center.getY());
  }

  public void setScale(double newScale) {
    Validate.isTrue(newScale != 0.0, "scale disallow 0.0;");
    if (_scale == newScale) {
      return;
    }
    _scale = newScale;
    damageTransform();
  }

  public void setBounds(Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    double hRatio = (double) _portSize.getWidth() / newBounds.getWidth();
    double vRatio = (double) _portSize.getHeight() / newBounds.getHeight();
    double newScale = hRatio < vRatio ? hRatio : vRatio;
    Point2D newCenter = new Point2D.Double(newBounds.getCenterX(),
            newBounds.getCenterY());
    if (newScale != _scale || newCenter != _center) {
      _center = newCenter;
      Validate.isTrue(newScale != 0.0, "scale disallow 0.0; ");
      _scale = newScale;
      damageTransform();
    }
  }

  public Rectangle2D getBounds() {
    Rectangle2D deviceBounds =
            new Rectangle2D.Double(0, 0, _portSize.getWidth(), _portSize.getHeight());
    Rectangle2D worldBounds = new Rectangle2D.Double();
    try {
      AffineTransform inverseTx = getTransform().createInverse();
      Point2D min = inverseTx.transform(
              new Point2D.Double(deviceBounds.getMinX(), deviceBounds.getMinY()), null);
      Point2D max = inverseTx.transform(
              new Point2D.Double(deviceBounds.getMaxX(), deviceBounds.getMaxY()), null);
      worldBounds.setFrameFromDiagonal(min, max);
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

  public void pushTransform(AffineTransform newTransform) {
    Validate.notNull(newTransform);
    _transformStack.add(newTransform);
    _transform = null;
  }

  public AffineTransform popTransform() {
    if (_transformStack.isEmpty()) {
      return null;
    }
    AffineTransform result;
    result = _transformStack.remove(_transformStack.size() - 1);
    _transform = null;
    return result;
  }

  public void fit() {
    setBounds(_structure.getBoundingBox());
  }

  public void viewMoveFraction(double x, double y) {
    Rectangle2D viewBounds = getBounds();
    double deltaX = viewBounds.getWidth() * x;
    double deltaY = viewBounds.getHeight() * y;
    Point2D newCenter = getCenter();
    setCenter(newCenter.getX() + deltaX, newCenter.getY() + deltaY);
  }

  private void damageTransform() {
    _transform = null;
    _basicTransform = null;
  }

  private AffineTransform lookupBasicTransform() {
    AffineTransform tx = new AffineTransform();
    if (_structure.isEmpty()) {
      return tx;
    }
    tx.concatenate(flipTransform());
    tx.concatenate(toCenterTransform());
    tx.concatenate(AffineTransform.getScaleInstance(_scale, _scale));
    tx.concatenate(viewToCenterTransform());
    return tx;
  }

  private AffineTransform flipTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, _portSize.getHeight());
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform toCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate((_portSize.getWidth() / 2), (_portSize.getHeight() / 2));
    return tx;
  }

  private AffineTransform viewToCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(-modelBounds().getCenterX(), -modelBounds().getCenterY());
    return tx;
  }

  private Rectangle2D modelBounds() {
    return _structure.getBoundingBox();
  }

  public double getVisibleRatio() {
    // TODO: add setter and more ...
    return 0.98;
  }
}

// vim: ts=2 sw=2 expandtab