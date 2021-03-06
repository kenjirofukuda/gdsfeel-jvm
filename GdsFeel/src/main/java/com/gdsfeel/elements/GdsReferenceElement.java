/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.elements;

import com.gdsfeel.Config;
import com.gdsfeel.Structure;
import com.gdsfeel.fx.container.GdsPoint;
import com.gdsfeel.fx.container.GdsPoints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Map;
import javafx.geometry.Rectangle2D;
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
  protected String _refName;
  private Structure _refStructure;

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

  public GdsPoint getOrigin2() {
    Validate.notEmpty(getVertices2());
    return getVertices2().get(0);
  }

  public final void setToNormalTransform() {
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
  public void setAttributes(Map<String, Object> attrs) {
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
    double a = _mag * cosRad;      // x scale
    double b = -_mag * sinRad;      // x shearing
    double c = Config.useFxProperty() ? getOrigin2().getX() : getOrigin().getX();  // x trans
    double d = _mag * sinRad;      // y scale
    double e = _mag * cosRad;      // y shearing
    double f = Config.useFxProperty() ? getOrigin2().getY() : getOrigin().getY();  // y trans
    /*
     * GDSII understands only the Y mirroring
     */
    /*
     * Reflecting about X means changing *Y*
     */
    if (_reflect) {
      b = -b;
      e = -e;
    }

    t.setTransform(a, d, b, e, c, f);
    return t;
  }

  public static Point2D[] calcOutlinePoints(java.awt.geom.Rectangle2D bounds, AffineTransform mat) {
    Point2D[] points =
            GdsElement.calcClosedOutlinePoints(bounds);
    Point2D[] result = new Point2D.Double[points.length];
    mat.transform(points, 0, result, 0, points.length);
    return result;
  }

  public static GdsPoints calcOutlinePoints(Rectangle2D bounds, AffineTransform mat) {
    GdsPoints points =
            GdsElement.calcClosedOutlinePoints(bounds);
    return points.transformedPoints(mat, null);
  }

  public String getReferenceName() {
    return _refName;
  }

  public Structure getReferenceStructure() {
    if (_refStructure == null) {
      if (getLibrary() == null) {
        return null;
      }
      _refStructure = getLibrary().structureNamed(_refName);
    }
    return _refStructure;
  }
}
