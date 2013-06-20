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
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Swing version
 *
 * @author kenjiro
 */
public final class ViewPort extends ViewPortBase {

  private static Log log = LogFactory.getLog(ViewPort.class);
  private Structure structure;

  public ViewPort(Structure structure) {
    super();
    this.structure = structure;
  }

  public void setPortSize(Dimension newSize) {
    setPortSize(newSize.getWidth(), newSize.getHeight());
  }

  public Point2D getCenter() {
    return new Point2D.Double(getCenterX(), getCenterY());
  }

  public void setBounds(Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    this.setBounds(newBounds.getX(), newBounds.getY(),
                   newBounds.getWidth(), newBounds.getHeight());
  }

  public Rectangle2D getBounds() {
    Rectangle2D deviceBounds =
            new Rectangle2D.Double(0, 0, getPortWidth(), getPortHeight());
    Rectangle2D worldBounds = new Rectangle2D.Double();
    try {
      AffineTransform inverseTx = getTransform().createInverse();
      Point2D min = inverseTx.transform(
              new Point2D.Double(deviceBounds.getMinX(), deviceBounds.getMinY()),
              null);
      Point2D max = inverseTx.transform(
              new Point2D.Double(deviceBounds.getMaxX(), deviceBounds.getMaxY()),
              null);
      worldBounds.setFrameFromDiagonal(min, max);
    }
    catch (NoninvertibleTransformException ex) {
      log.error(ex);
    }
    return worldBounds;
  }

  public void fit() {
    resetPortCenter();
    setBounds(modelBounds());
  }

  public void viewMoveFraction(double x, double y) {
    Rectangle2D viewBounds = getBounds();
    double deltaX = viewBounds.getWidth() * x;
    double deltaY = viewBounds.getHeight() * y;
    Point2D newCenter = getCenter();
    setCenter(newCenter.getX() + deltaX, newCenter.getY() + deltaY);
  }

  private Rectangle2D modelBounds() {
    return structure.getBoundingBox();
  }
}
// vim: ts=2 sw=2 expandtab

