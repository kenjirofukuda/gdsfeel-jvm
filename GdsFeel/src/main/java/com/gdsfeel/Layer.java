/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author kenjiro
 */
public class Layer {

  private boolean _selectable;
  private Boolean _visible;
  private java.awt.Color _color;
  private int _gdsno;

  public Layer() {
    this(-1);
  }

  public Layer(int gdsno) {
    _selectable = true;
    _visible = true;
    _color = java.awt.Color.lightGray;
    _gdsno = gdsno;
  }

  public int getNumber() {
    return _gdsno;
  }

  public java.awt.Color getColor() {
    return _color;
  }

  public void fillDefaultAttributes(Map<String, Object> attrs) {
    attrs.put("selectable", true);
    attrs.put("visible", true);
    attrs.put("gdsno", -1);
    // element
    attrs.put("color", java.awt.Color.lightGray);
  }

  public void exportAttributes(Map<String, Object> attrs) {
    attrs.put("selectable", _selectable);
    attrs.put("visible", _visible);
    attrs.put("gdsno", _gdsno);
    // element
    attrs.put("color", _color);
  }

  public void importAttributes(Map<String, Object> attrs) {
    _selectable = (Boolean) attrs.get("selectable");
    _visible = (Boolean) attrs.get("visible");
    _gdsno = (Integer) attrs.get("gdsno");
    // element
    _color = (java.awt.Color) attrs.get("color");
  }

  public void loadFromXmlElement(Element e) {
    Map<String, Object> attrs = new HashMap<>();
    fillDefaultAttributes(attrs);

    NodeList nl = e.getElementsByTagName("color");

    Element colorElement = (Element) nl.item(0);
    if (colorElement != null) {
      Map<String, Object> colorAttrs = new HashMap<>();
      String[] attrNames = new String[]{"r", "g", "b", "a"};
      for (String attrName : attrNames) {
        if (colorElement.hasAttribute(attrName)) {
          colorAttrs.put(attrName, Float.parseFloat(colorElement.getAttribute(attrName)));
        }
      }
      java.awt.Color newColor = new java.awt.Color(
              (Float) colorAttrs.get("r"),
              (Float) colorAttrs.get("g"),
              (Float) colorAttrs.get("b"),
              (Float) colorAttrs.get("a"));
      attrs.put("color", newColor);
    }

    for (String attrName : integerAttributeNames()) {
      if (e.hasAttribute(attrName)) {
        attrs.put(attrName, Integer.parseInt(e.getAttribute(attrName)));
      }
    }

    for (String attrName : booleanAttributeNames()) {
      if (e.hasAttribute(attrName)) {
        attrs.put(attrName, Boolean.parseBoolean(e.getAttribute(attrName)));
      }
    }
    importAttributes(attrs);
  }

  private String[] booleanAttributeNames() {
    return new String[]{
      "selectable",
      "visible"
    };
  }

  private String[] integerAttributeNames() {
    return new String[]{
      "gdsno"
    };
  }
}
