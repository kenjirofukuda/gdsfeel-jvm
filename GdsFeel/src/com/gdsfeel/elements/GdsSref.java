/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gdsfeel.elements;

import com.gdsfeel.Structure;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author kenjiro
 */

public class GdsSref extends GdsReferenceElement {
  private static Log log = LogFactory.getLog(GdsSref.class);
  private String _refName;

  public GdsSref() {
    super();
    _refName = "";
  }


  public String getReferenceName() {
    return _refName;
  }


  @Override
  public Point2D[] outlinePoints() {
    return calcOutlinePoints(structureBoundingBox(), getTransform());
  }


  public Rectangle2D structureBoundingBox() {
    Rectangle2D result = resolveStructure().getBoundingBox();
    return result;
  }


  public Structure resolveStructure() {
    if (getLibrary() == null) {
      return null;
    }
    return getLibrary().structureNamed(_refName);
  }


  @Override
  public void setAttributes(Map<String,Object> attrs) {
    super.setAttributes(attrs);
    if (attrs.containsKey("sname")) {
      _refName = (String) attrs.get("sname");
    }
  }
}

