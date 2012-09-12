/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package com.gdsfeel.elements;


import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author kenjiro
 */

public class GdsReferenceElement extends GdsElement {
  private static Log log = LogFactory.getLog(GdsReferenceElement.class);
  private double _angle;
  private double _mag;
  private boolean _reflect;
  private AffineTransform _transform;


  protected GdsReferenceElement() {
    setToNormalTransform();
  }


  public double getAngle() {
    return _angle;
  }


  public double getMag() {
    return _mag;
  }


  public boolean isReflected() {
    return _reflect;
  }


  public Point2D getOrigin() {
    Validate.notEmpty(getVertices());
    return (getVertices())[0];
  }


  public void setToNormalTransform() {
    _angle = 0.0;
    _mag = 1.0;
    _reflect = false;
    _transform = null;
  }

  
  @Override
  protected void clearGeometryCache() {
    super.clearGeometryCache();
    _transform = null;
  }


  @Override
  public void setAttributes(Map<String,Object> attrs) {
    super.setAttributes(attrs);
    if (attrs.containsKey("angle")) {
      _angle = (Double) attrs.get("angle");
    }

    if (attrs.containsKey("mag")) {
      _mag = (Double) attrs.get("mag");
    }

    if (attrs.containsKey("reflected")) {
      _reflect = (Boolean) attrs.get("reflected");
    }
    _transform = null;
  }


  public AffineTransform getTransform() {
    if (_transform == null) {
      _transform = lookupTransform();
    }
    return _transform;
  }


  public AffineTransform lookupTransform() {
    AffineTransform t = new AffineTransform();
    double rad = _angle * Math.PI / 180.0;
    double cosRad = Math.cos(rad);
    double sinRad = Math.sin(rad);
    double a =  _mag * cosRad;
    double b = -_mag * sinRad;
    double c = getOrigin().getX();
    double d =  _mag * sinRad;
    double e =  _mag * cosRad;
    double f = getOrigin().getY();
    /* GDSII understands only the Y mirroring */
    /* Reflecting about X means changing *Y* */
    if (_reflect) {
      b = -b;
      e = -e;
    }

    t.setTransform(a, d, b, e, c, f);
    return t;
  }

  
  public static Point2D[] calcOutlinePoints(Rectangle2D bounds, AffineTransform mat) {
    Point2D[] points =
      GdsElement.calcClosedOutlinePoints(bounds);
    Point2D[] result = new Point2D.Double[points.length];
    mat.transform(points, 0, result, 0, points.length);
    return result;
  }
}




