/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.elements;

import com.gdsfeel.fx.container.GdsPoints;
import java.awt.geom.Point2D;
import java.util.Map;
import javafx.geometry.Rectangle2D;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class GdsSref extends GdsReferenceElement {

  private static Log log = LogFactory.getLog(GdsSref.class);

  public GdsSref() {
    super();
    _refName = "";
  }

  @Override
  public Point2D[] outlinePoints() {
    return calcOutlinePoints(structureBoundingBox(), getTransform());
  }

  @Override
  public GdsPoints outlinePoints2() {
    return calcOutlinePoints(structureBoundingBox2(), getTransform());
  }

  public java.awt.geom.Rectangle2D structureBoundingBox() {
    java.awt.geom.Rectangle2D result = getReferenceStructure().getBoundingBox();
    return result;
  }

  public Rectangle2D structureBoundingBox2() {
    return getReferenceStructure().getBoundingBox2();
  }

  @Override
  public void setAttributes(Map<String, Object> attrs) {
    super.setAttributes(attrs);
    if (attrs.containsKey("sname")) {
      _refName = (String) attrs.get("sname");
    }
  }
}
