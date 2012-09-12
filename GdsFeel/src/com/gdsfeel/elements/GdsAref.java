/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gdsfeel.elements;

import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class GdsAref extends GdsSref {
  private static Log log = LogFactory.getLog(GdsAref.class);
  private int _rowCount;
  private int _columnCount;
  private double _rowStep;
  private double _columnStep;
  private AffineTransform[] _transforms;


  public GdsAref() {
    super();
    _rowCount = 1;
    _columnCount = 1;
    _rowStep = 0.0;
    _columnStep = 0.0;
    _transforms = null;
  }


  public int getRowCount() {
    return _rowCount;
  }


  public void setRowCount(int newCount) {
    if (_rowCount == newCount) return;
    _rowCount = newCount;
    clearGeometryCache();
  }


  public int getColumnCount() {
    return _columnCount;
  }


  public void setColumnCount(int newCount) {
    if (_columnCount == newCount) return;
    _columnCount = newCount;
    clearGeometryCache();
  }


  public double getRowStep() {
    return _rowStep;
  }


  public void setRowStep(double newStep) {
    if (_rowStep == newStep) return;
    _rowStep = newStep;
    clearGeometryCache();
  }


  public double getColumnStep() {
    return _columnStep;
  }


  public void setColumnStep(double newStep) {
    if (_columnStep == newStep) return;
    _columnStep = newStep;
    clearGeometryCache();
  }


  public AffineTransform[] getTransforms() {
    if (_transforms == null) {
      _transforms = lookupTransforms();
    }
    return _transforms;
  }


  public boolean canRepeat() {
    return getRowCount() > 1 || getColumnCount() > 1;
  }


  private AffineTransform[] lookupOffsetTransforms() {
    List<AffineTransform> transforms = new ArrayList<AffineTransform>();
    for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
      for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
        double xOffset = colIndex * getColumnStep();
        double yOffset = rowIndex * getRowStep();
        AffineTransform newTrtansform = new AffineTransform();
        newTrtansform.translate(xOffset, yOffset);
        transforms.add(newTrtansform);
      }
    }
    return transforms.toArray(new AffineTransform[0]);
  }


  public AffineTransform[] lookupTransforms() {
    List<AffineTransform> transforms = new ArrayList<AffineTransform>();
    for (int colIndex = 0; colIndex < getColumnCount(); colIndex++) {
      for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
        double xOffset = colIndex * getColumnStep();
        double yOffset = rowIndex * getRowStep();
        AffineTransform newTrtansform = new AffineTransform(getTransform());
        newTrtansform.translate(xOffset, yOffset);
        transforms.add(newTrtansform);
      }
    }
    return transforms.toArray(new AffineTransform[0]);
  }


//  public Rectangle2D[] lookupBoundingBoxes() {
//    List<Rectangle2D> boundsa = new ArrayList<Rectangle2D>();
//    for (AffineTransform mat : getTransforms()) {
//        calcOutlinePoints(structureBoundingBox(), getTransform());
//    }
//    return boundsa.toArray(new Rectangle2D[0]);
//  }

  @Override
  protected void clearGeometryCache() {
    super.clearGeometryCache();
    _transforms = null;
  }

  
  @Override
  public void setAttributes(Map<String,Object> attrs) {
    super.setAttributes(attrs);
    if (attrs.containsKey("cols")) {
      _columnCount = (Integer) attrs.get("cols");
    }
    if (attrs.containsKey("rows")) {
      _rowCount = (Integer) attrs.get("rows");
    }
    if (attrs.containsKey("column-spacing")) {
      _columnStep = (Double) attrs.get("column-spacing");
    }
    if (attrs.containsKey("row-spacing")) {
      _rowStep = (Double) attrs.get("row-spacing");
    }
  }
}

