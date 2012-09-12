/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gdsfeel;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class Viewport {
  private static Log log = LogFactory.getLog(Viewport.class);
  private Structure _structure;
  private ArrayList<AffineTransform> _transformStack;
  private Dimension _portSize;
  private Point2D _center;
  private double _scale;
  private AffineTransform _transform;
  private AffineTransform _basicTransform;

  public Viewport(Structure structure) {
    _structure = structure;
    
  }

  public void setPortSize(Dimension newSize) {
    Validate.notNull(newSize);
    // FIXME
  }

  public void setCenter(Point2D newCenter) {
    Validate.notNull(newCenter);
    // FIXME
  }

  public void setScale(double newScale) {
    // FIXME
  }

  public void setBounds(Rectangle2D newBounds) {
    Validate.notNull(newBounds);
    // FIXME
  }

  public AffineTransform getBasicTransform() {
    return null; // FIXME
  }

  public AffineTransform getTransform() {
    return null; // FIXME
  }

  public void pushTransform(AffineTransform newTransform) {
    Validate.notNull(newTransform);
    // FIXME
  }

  public AffineTransform popTransform() {
    // FIXME
    return null;
  }

  public void fit() {
    // FIXME
  }
}
