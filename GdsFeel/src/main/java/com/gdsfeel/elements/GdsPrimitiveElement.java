/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.elements;

import java.awt.Color;
import java.util.Map;

/**
 *
 * @author cs
 */
public class GdsPrimitiveElement extends GdsElement {

  private int _datatype;
  private int _layerNumber;

  protected GdsPrimitiveElement() {
    super();
    _datatype = 0;
    _layerNumber = -1;
  }

  public int getLayerNumber() {
    return _layerNumber;
  }

  @Override
  public void setAttributes(Map<String, Object> attrs) {
    super.setAttributes(attrs);
    if (attrs.containsKey("datatype")) {
      _datatype = (Integer) attrs.get("datatype");
    }
    if (attrs.containsKey("layerNumber")) {
      _layerNumber = (Integer) attrs.get("layerNumber");
    }
  }

  public static Color lookupFrameColor(GdsPrimitiveElement element) {
    return element.getStructure().colorForLayerNumber(element.getLayerNumber());
  }

  public static Color lookupFrameColor(GdsElement element, java.awt.Color fallbackColor) {
    Color result = fallbackColor;
    try {
      GdsPrimitiveElement pe = (GdsPrimitiveElement) element;
      result = GdsPrimitiveElement.lookupFrameColor(pe);
    }
    catch (ClassCastException ex) {
    }
    return result;
  }
}
