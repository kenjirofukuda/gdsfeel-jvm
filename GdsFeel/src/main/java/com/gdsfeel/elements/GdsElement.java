/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.elements;

import com.gdsfeel.Config;
import com.gdsfeel.GdsObject;
import com.gdsfeel.Library;
import com.gdsfeel.Structure;
import com.gdsfeel.fx.container.GdsPoints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author kenjiro
 */
public class GdsElement extends GdsObject<Structure, GdsObject> {

  private static Log log = LogFactory.getLog(GdsElement.class);
  private Point2D[] _vertices;
  private GdsPoints vertices;
  private int _keyNumber;
  private java.awt.geom.Rectangle2D _boundingBox;
  private ObjectProperty<Rectangle2D> boundingBox2;
  private Map<String, Object> _runtimeMap;

  public GdsElement() {
    super();
    _vertices = new Point2D.Double[0];
    vertices = new GdsPoints(this, "vertices");
    boundingBox2 = new SimpleObjectProperty<>(this, "boundingBox2");
    _keyNumber = -1;
  }

  public Structure getStructure() {
    return getParent();
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

  public Point2D[] getVertices() {
    return _vertices;
  }

  public GdsPoints getVertices2() {
    return vertices;
  }

  public Point2D[] outlinePoints() {
    return getVertices();
  }

  public GdsPoints outlinePoints2() {
    return getVertices2();
  }

  public java.awt.geom.Rectangle2D getBoundingBox() {
    if (_boundingBox == null) {
      _boundingBox = lookupBoundingBox();
    }
    return _boundingBox;
  }

  public Rectangle2D getBoundingBox2() {
    if (boundingBox2.get() == null) {
      boundingBox2.set(lookupBoundingBox2());
    }
    return boundingBox2.get();
  }

  protected void clearGeometryCache() {
    _boundingBox = null;
  }

  protected java.awt.geom.Rectangle2D lookupBoundingBox() {
    return calcBoundingBox(outlinePoints());
  }

  protected Rectangle2D lookupBoundingBox2() {
    return outlinePoints2().getBounds();
  }
  public static double BIG_VAL = Integer.MAX_VALUE / 2.0;

  public static java.awt.geom.Rectangle2D calcBoundingBox(Point2D[] outlinePoints) {
    // TOUCH: scala
    double xmin = BIG_VAL;
    double xmax = -BIG_VAL;
    double ymin = BIG_VAL;
    double ymax = -BIG_VAL;
    for (Point2D p : outlinePoints) {
      if (p.getX() < xmin) {
        xmin = p.getX();
      }
      if (p.getX() > xmax) {
        xmax = p.getX();
      }
      if (p.getY() < ymin) {
        ymin = p.getY();
      }
      if (p.getY() > ymax) {
        ymax = p.getY();
      }
    }
    java.awt.geom.Rectangle2D result = new java.awt.geom.Rectangle2D.Double();
    result.setFrameFromDiagonal(xmin, ymin, xmax, ymax);
    return result;
  }

  public static Point2D[] calcClosedOutlinePoints(java.awt.geom.Rectangle2D bounds) {
    // TOUCH: scala
    Point2D[] result = new Point2D.Double[5];
    result[0] = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
    result[1] = new Point2D.Double(bounds.getMinX(), bounds.getMaxY());
    result[2] = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
    result[3] = new Point2D.Double(bounds.getMaxX(), bounds.getMinY());
    result[4] = new Point2D.Double(result[0].getX(), result[0].getY());
    return result;
  }

  public static GdsPoints calcClosedOutlinePoints(Rectangle2D bounds) {
    // TODO: move to GdsPoints
    GdsPoints result = new GdsPoints();
    result.add(bounds.getMinX(), bounds.getMinY());
    result.add(bounds.getMinX(), bounds.getMaxY());
    result.add(bounds.getMaxX(), bounds.getMaxY());
    result.add(bounds.getMaxX(), bounds.getMinY());
    result.add(result.get(0).getX(), result.get(0).getY());
    return result;
  }

  public void setAttributes(Map<String, Object> attrs) {
    if (attrs.containsKey("keyNumber")) {
      _keyNumber = (Integer) attrs.get("keyNumber");
    }
    if (Config.useFxProperty()) {
      vertices.clear();
      vertices.addAll((GdsPoints) attrs.get("vertices"));
    }
    else {
      _vertices = (Point2D[]) attrs.get("vertices");
    }

    clearGeometryCache();
  }

  public static GdsElement fromXml(org.w3c.dom.Element e) {
    Map<String, Object> attrs = new HashMap<>();
    elementToAttributes(e, attrs);
    GdsElement el = newElementFromTypeCode((String) attrs.get("type"));
    if (el == null) {
      return null;
    }
    el.setAttributes(attrs);
    return el;
  }

  private static void elementToAttributes(
          Element e,
          Map<String, Object> attrs) {

    if (!e.hasAttribute("type")) {
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
    if (Config.useFxProperty()) {
      GdsPoints points = new GdsPoints();

      getXyArray(xynl, points);
      attrs.put("vertices", points);
    }
    else {
      List<Point2D> points = new ArrayList<>();

      getXyArray(xynl, points);
      attrs.put("vertices", points.toArray(new Point2D.Double[0]));
    }

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
    for (int ci = 0; ci < xyNodeList.getLength(); ci++) {
      Element p = (Element) xyNodeList.item(ci);
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

  private static void getXyArray(NodeList xyNodeList, GdsPoints points) {
    for (int ci = 0; ci < xyNodeList.getLength(); ci++) {
      Element p = (Element) xyNodeList.item(ci);
      String xyStr = p.getTextContent();
      String[] items = StringUtils.split(xyStr, " ");
      if (items.length != 2) {
        log.error("invarid xy format");
        return;
      }
      double x, y;
      try {
        x = Double.parseDouble(items[0]);
        y = Double.parseDouble(items[1]);
      }
      catch (NumberFormatException ex) {
        log.error("invarid xy format");
        return;
      }
      points.add(x, y);
    }
  }

  private static void getStrans(
          NodeList stransNodeList,
          Map<String, Object> attrs) {
    for (int ci = 0; ci < stransNodeList.getLength(); ci++) {
      Element e = (Element) stransNodeList.item(ci);
      for (String attrName : new String[]{"cols", "rows"}) {
        if (e.hasAttribute(attrName)) {
          attrs.put(attrName, Integer.parseInt(e.getAttribute(attrName)));
        }
      }
      for (String attrName : new String[]{"column-spacing", "row-spacing"}) {
        if (e.hasAttribute(attrName)) {
          attrs.put(attrName, Double.parseDouble(e.getAttribute(attrName)));
        }
      }
    }
  }

  private static String[] integerAttributeNames() {
    return new String[]{
      "datatype",
      "pathtype",
      "keyNumber",
      "layerNumber"
    };
  }

  private static String[] doubleAttributeNames() {
    return new String[]{
      "mag",
      "width",
      "angle"
    };
  }

  private static String[] booleanAttributeNames() {
    return new String[]{
      "reflected"
    };
  }

  private static String[] stringAttributeNames() {
    return new String[]{
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

  public void setRuntimeProperty(String key, Object value) {
    if (_runtimeMap == null) {
      _runtimeMap = new HashMap<>();
    }
    _runtimeMap.put(key, value);
  }

  public Object getRuntimeProperty(String key) {
    if (_runtimeMap == null) {
      _runtimeMap = new HashMap<>();
    }
    if (_runtimeMap.containsKey(key)) {
      return _runtimeMap.get(key);
    }
    return null;
  }

  public static void splitPlimitivesAndReferences(
          GdsElement[] elements,
          ArrayList<GdsElement> primitives,
          ArrayList<GdsElement> references) {
    for (GdsElement e : elements) {
      if (e instanceof GdsSref || e instanceof GdsAref) {
        references.add(e);
      }
      else {
        primitives.add(e);
      }
    }
  }

  public static void splitPlimitivesAndReferences(
          Collection<GdsElement> elements,
          ArrayList<GdsElement> primitives,
          ArrayList<GdsElement> references) {
    splitPlimitivesAndReferences(elements.toArray(new GdsElement[0]),
                                 primitives, references);

  }
}
