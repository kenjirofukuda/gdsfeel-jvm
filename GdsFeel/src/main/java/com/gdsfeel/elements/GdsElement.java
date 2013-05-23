/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.elements;

import com.gdsfeel.GdsObject;
import com.gdsfeel.Library;
import com.gdsfeel.Structure;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author kenjiro
 */
public class GdsElement extends GdsObject {

  private static Log log = LogFactory.getLog(GdsElement.class);

  private Point2D[] _vertices;
  private int _keyNumber;
  private Rectangle2D _boundingBox;
  protected String _refName;

  
  public GdsElement() {
    super();
    _vertices = new Point2D.Double[0];
    _keyNumber = -1;
  }


  public Structure getStructure() {
    return (Structure) getParent();
  }


  public Library getLibrary() {
    if (getStructure() == null) {
      return null;
    }
    return getStructure().getLibrary();
  }


  public int getKeyNumber() {
    return _keyNumber;
  }

 
  public void setVertices(Collection<Point2D> points) {
    setVertices(points.toArray(new Point2D.Double[0]));
  }


  public void setVertices(Point2D[] points) {
    _vertices = points;
    clearGeometryCache();
  }

  
  public Point2D[] getVertices() {
    return _vertices;
  }


  public Point2D[] outlinePoints() {
    return getVertices();
  }


  public Rectangle2D getBoundingBox() {
    if (_boundingBox == null) {
      _boundingBox = lookupBoundingBox();
    }
    return _boundingBox;
  }

  
  protected void clearGeometryCache() {
    _boundingBox = null;
  }


  protected Rectangle2D lookupBoundingBox() {
    return calcBoundingBox(outlinePoints());
  }

  public static double BIG_VAL = Integer.MAX_VALUE / 2.0;
  
  public static Rectangle2D calcBoundingBox(Point2D[] outlinePoints) {
    // TOUCH: scala
    double xmin = BIG_VAL;
    double xmax = -BIG_VAL;
    double ymin = BIG_VAL;
    double ymax = -BIG_VAL;
    for (Point2D p : outlinePoints) {
      if (p.getX() < xmin) xmin = p.getX();
      if (p.getX() > xmax) xmax = p.getX();
      if (p.getY() < ymin) ymin = p.getY();
      if (p.getY() > ymax) ymax = p.getY();
    }
    Rectangle2D result =  new Rectangle2D.Double();
    result.setFrameFromDiagonal(xmin, ymin, xmax, ymax);
    return result;
  }

  
  public static Point2D[] calcClosedOutlinePoints(Rectangle2D bounds) {
    // TOUCH: scala
    Point2D[] result = new Point2D.Double[5];
    result[0] = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
    result[1] = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
    result[2] = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
    result[3] = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
    result[4] = new Point2D.Double(result[0].getX(), result[0].getY());
    return result;
  }

  
  public void setAttributes(Map<String,Object> attrs) {
    if (attrs.containsKey("keyNumber")) {
      _keyNumber = (Integer) attrs.get("keyNumber");
    }
    _vertices = (Point2D[]) attrs.get("vertices");
    clearGeometryCache();
  }


  public static GdsElement fromXml(org.w3c.dom.Element e) {
    Map<String, Object> attrs = new HashMap<>();
    elementToAttributes(e, attrs);
    GdsElement el = newElementFromTypeCode((String) attrs.get("type"));
    if (el == null) return null;
    el.setAttributes(attrs);
    return el;
  }


  private static void elementToAttributes(
          Element e,
          Map<String,Object> attrs) {

    if (! e.hasAttribute("type")) {
      log.error("missing type field");
      return;
    }

    String type = e.getAttribute("type");
    attrs.put("type", type);

    NodeList vnl = e.getElementsByTagName("vertices");
    if (vnl.getLength() == 0) {
      log.error("vertices not found");
      return;
    }

    NodeList xynl = e.getElementsByTagName("xy");
    List<Point2D> points = new ArrayList<>();

    getXyArray(xynl, points);
    attrs.put("vertices", points.toArray(new Point2D.Double[0]));

    NodeList snl = e.getElementsByTagName("ashape");
    if (snl.getLength() > 0) {
      getStrans(snl, attrs);
    }


    for (String attrName : integerAttributeNames()) {
      if (e.hasAttribute(attrName)) {
        attrs.put(attrName, Integer.parseInt(e.getAttribute(attrName)));
      }
    }

    for (String attrName : doubleAttributeNames()) {
      if (e.hasAttribute(attrName)) {
        attrs.put(attrName, Double.parseDouble(e.getAttribute(attrName)));
      }
    }

    for (String attrName : booleanAttributeNames()) {
      if (e.hasAttribute(attrName)) {
        attrs.put(attrName, Boolean.parseBoolean(e.getAttribute(attrName)));
      }
    }

    for (String attrName : stringAttributeNames()) {
      if (e.hasAttribute(attrName)) {
        attrs.put(attrName, e.getAttribute(attrName));
      }
    }
  }


  private static void getXyArray(NodeList xyNodeList, List<Point2D> points) {
    for (int ci = 0; ci < xyNodeList.getLength() ; ci++) {
      Element p = (Element)xyNodeList.item(ci);
      String xyStr = p.getTextContent();
      String[] items = StringUtils.split(xyStr, " ");
      if (items.length != 2) {
        log.error("invarid xy format");
        return;
      }
      double x, y = 0;
      try {
        x = Double.parseDouble(items[0]);
        y = Double.parseDouble(items[1]);
      }
      catch (NumberFormatException ex) {
        log.error("invarid xy format");
        return;
      }
      Point2D pt = new Point2D.Double(x, y);
      points.add(pt);
    }
  }


  private static void getStrans(
      NodeList stransNodeList,
      Map<String,Object> attrs) {
    for (int ci = 0; ci < stransNodeList.getLength() ; ci++) {
      Element e = (Element) stransNodeList.item(ci);
      for (String attrName : new String[] {"cols", "rows"}) {
        if (e.hasAttribute(attrName)) {
          attrs.put(attrName, Integer.parseInt(e.getAttribute(attrName)));
        }
      }
      for (String attrName : new String[] {"column-spacing", "row-spacing"}) {
        if (e.hasAttribute(attrName)) {
          attrs.put(attrName, Double.parseDouble(e.getAttribute(attrName)));
        }
      }
    }
  }


  private static String[] integerAttributeNames() {
    return new String[] {
      "datatype",
      "pathtype",
      "keyNumber",
      "layerNumber"
    };
  }


  private static String[] doubleAttributeNames() {
    return new String[] {
      "mag",
      "width",
      "angle"
    };
  }


  private static String[] booleanAttributeNames() {
    return new String[] {
      "reflected"
    };
  }


  private static String[] stringAttributeNames() {
    return new String[] {
      "sname",
      "text"
    };
  }


  private static GdsElement newElementFromTypeCode(String type) {
    if (type.equalsIgnoreCase("path")) {
      return new GdsPath();
    }
    if (type.equalsIgnoreCase("boundary")) {
      return new GdsBoundary();
    }
    if (type.equalsIgnoreCase("sref")) {
      return new GdsSref();
    }
    if (type.equalsIgnoreCase("aref")) {
      return new GdsAref();
    }
    log.warn(type + ": Can't current handled type!!!");
    return null;
  }

  public String getReferenceName() {
    return _refName;
  }

  public Structure resolveStructure() {
    if (getLibrary() == null) {
      return null;
    }
    return getLibrary().structureNamed(_refName);
  }

}
