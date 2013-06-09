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
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Swing version
 *
 * @author kenjiro
 */
public class ViewPort {

  private static Log log = LogFactory.getLog(ViewPort.class);
  private Structure structure;
  private ArrayList<AffineTransform> transformStack;
  private Dimension portSize;
  private Point2D center;
  private double scale;
  private AffineTransform transform;
  private AffineTransform basicTransform;
  private Point2D portCenter;

  public ViewPort(Structure structure) {
    this.structure = structure;
    transformStack = new ArrayList<>();
    portSize = new Dimension(0, 0);
    scale = 1.0;
    center = new Point2D.Double(0, 0);
    portCenter = new Point2D.Double(0, 0);
  }

  public void setPortSize(Dimension newSize) {
    Validate.notNull(newSize);
    Validate.isTrue(newSize.getWidth() != 0);
    Validate.isTrue(newSize.getHeight() != 0);
    if (portSize.equals(newSize)) {
      return;
    }
    portSize.setSize(newSize);
    portCenter.setLocation(portSize.getWidth() / 2.0, portSize.getHeight() / 2.0);
    damageTransform();
  }

  public void setCenter(double x, double y) {
    setCenter(new Point2D.Double(x, y));
  }

  public void setCenter(Point2D newCenter) {
    Validate.notNull(newCenter);
    if (center.equals(newCenter)) {
      return;
    }
    center.setLocation(newCenter);
    damageTransform();
  }

  public Point2D getCenter() {
    return new Point2D.Double(center.getX(), center.getY());
  }

  public void setScale(double newScale) {
    Validate.isTrue(newScale != 0.0, "scale disallow 0.0;");
    if (scale == newScale) {
      return;
    }
    if (newScale <= 0.001) {
      log.error(String.format("newScale is %f convert to safe margin value ==> 0.001", newScale));
      newScale = 0.001;
    }
    scale = newScale;
    damageTransform();
  }

  public double getScale() {
    return scale;
  }

  public void setPortCenter(double x, double y) {
    portCenter.setLocation(x, portSize.getHeight() - y);
    damageTransform();
  }

  public void resetPortCenter() {
    setPortCenter(portSize.getWidth() / 2.0, portSize.getHeight() / 2.0);
  }

  public void zoom(double ratio) {
    setScale(getScale() * ratio);
  }

  public void setBounds(Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    double hRatio = (double) portSize.getWidth() / newBounds.getWidth();
    double vRatio = (double) portSize.getHeight() / newBounds.getHeight();
    double newScale = hRatio < vRatio ? hRatio : vRatio;
    Point2D newCenter = new Point2D.Double(newBounds.getCenterX(),
                                           newBounds.getCenterY());
    if (newScale != scale || (!newCenter.equals(center))) {
      center = newCenter;
      Validate.isTrue(newScale != 0.0, "scale disallow 0.0; ");
      scale = newScale;
      damageTransform();
    }
  }

  public Rectangle2D getBounds() {
    Rectangle2D deviceBounds =
            new Rectangle2D.Double(0, 0, portSize.getWidth(), portSize.getHeight());
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
    if (basicTransform == null) {
      basicTransform = lookupBasicTransform();
    }
    return basicTransform;
  }

  public AffineTransform getTransform() {
    if (transform == null) {
      AffineTransform newTransform = new AffineTransform();
      newTransform.concatenate(getBasicTransform());
      for (AffineTransform tx : transformStack) {
        newTransform.concatenate(tx);
      }
      transform = newTransform;
    }
    return transform;
  }

  public void pushTransform(AffineTransform newTransform) {
    Validate.notNull(newTransform);
    transformStack.add(newTransform);
    transform = null;
  }

  public AffineTransform popTransform() {
    if (transformStack.isEmpty()) {
      return null;
    }
    AffineTransform result;
    result = transformStack.remove(transformStack.size() - 1);
    transform = null;
    return result;
  }

  public void fit() {
    setBounds(structure.getBoundingBox());
  }

  public void viewMoveFraction(double x, double y) {
    Rectangle2D viewBounds = getBounds();
    double deltaX = viewBounds.getWidth() * x;
    double deltaY = viewBounds.getHeight() * y;
    Point2D newCenter = getCenter();
    setCenter(newCenter.getX() + deltaX, newCenter.getY() + deltaY);
  }

  private void damageTransform() {
    transform = null;
    basicTransform = null;
  }

  private AffineTransform lookupBasicTransform() {
    AffineTransform tx = new AffineTransform();
    if (structure.isEmpty()) {
      return tx;
    }
    tx.concatenate(flipTransform());
    tx.concatenate(toCenterTransform());
    tx.concatenate(AffineTransform.getScaleInstance(
            scale * getVisibleRatio(), scale * getVisibleRatio()));
    tx.concatenate(viewToCenterTransform());
    return tx;
  }

  private AffineTransform flipTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, portSize.getHeight());
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform toCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(portCenter.getX(), portCenter.getY());
    return tx;
  }

  private AffineTransform viewToCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(-center.getX(), -center.getY());
    return tx;
  }

  private Rectangle2D modelBounds() {
    return structure.getBoundingBox();
  }

  public double getVisibleRatio() {
    // TODO: add setter and more ...
    return 0.98;
  }
}
// vim: ts=2 sw=2 expandtab

