/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui.swing;

import com.gdsfeel.elements.GdsAref;
import com.gdsfeel.elements.GdsBoundary;
import com.gdsfeel.elements.GdsElement;
import com.gdsfeel.elements.GdsPath;
import com.gdsfeel.elements.GdsPrimitiveElement;
import com.gdsfeel.elements.GdsSref;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
class GdsElementDrawer<T extends GdsElement> {

  private static Log log = LogFactory.getLog(GdsElementDrawer.class);
  protected T element;
  protected StructureView view;
  private Color _frameColor;
  protected GeneralPath _framePath;
  private Paint _paint;
  
  public void initWith(GdsElement element, StructureView view) {
    this.element = (T) element;
    this.view = view;
  }

  public final void fullDrawOn(Graphics2D g) {
    Color savedColor = g.getColor();
    drawOn(g);
    g.setColor(savedColor);
  }
    
  public void drawOn(Graphics2D g) {
    if (canPaint()) {
      g.setPaint(getPaint());
      paintOn(g);
    }
    if (canStroke()) {
      g.setColor(getFrameColor());
      strokeFrameOn(g);
    }
  }

  protected boolean canStroke() {
    if (canPaint() && (getPaint() != null)) {
      if (getPaint().equals(getFrameColor())) {
        // does not stroke same area same color
        return false;
      }
    }
    return true;
  }

  protected boolean canPaint() {
    return false; // _paint != null;
  }
  
  public Color getFrameColor() {
    if (_frameColor == null) {
      _frameColor = lookupFrameColor();
    }
    return _frameColor;
  }

  public Paint getPaint() {
    // FIXME:
    return _paint;
  }
  
  private Color lookupFrameColor() {
    Color result = Color.white;
    try {
      GdsPrimitiveElement pe = (GdsPrimitiveElement) element;
      result = pe.getStructure().colorForLayerNumber(pe.getLayerNumber());
    }
    catch (ClassCastException ex) {
    }
    return result;
  }

  // Overridable
  public void strokeFrameOn(Graphics2D g) {
    strokePoints(g, element.outlinePoints(), getViewPortTransform());
  }

  // Overridable
  public void paintOn(Graphics2D g) {
    if (_framePath == null) {
      _framePath = new GeneralPath();
      addPoints(_framePath, element.outlinePoints());
    }
    Shape s = _framePath;
    if (getViewPortTransform() != null) {
      s = getViewPortTransform().createTransformedShape(_framePath);
    }
    g.fill(s);
  }
  
  
  public void strokePoints(Graphics2D g, Point2D[] points, AffineTransform tx) {
    if (_framePath == null) {
      _framePath = new GeneralPath();
      addPoints(_framePath, points);
    }
    Shape s = _framePath;
    if (tx != null) {
      s = tx.createTransformedShape(_framePath);
    }
    g.draw(s);
  }

  public void strokePoints(Graphics2D g, Point2D[] points) {
    strokePoints(g, points, null);
  }

  protected void addPoints(GeneralPath path, Point2D[] points) {
    int index = 0;
    for (Point2D pt : points) {
      if (index == 0) {
        path.moveTo(pt.getX(), pt.getY());
      }
      else {
        path.lineTo(pt.getX(), pt.getY());
      }
      index += 1;
    }
  }

  protected AffineTransform getViewPortTransform() {
    return view.getViewPort().getTransform();
  }

  static Class<? extends GdsElementDrawer> drawerClassForElement(GdsElement e) {
    Class<? extends GdsElementDrawer> drawerClass = GdsElementDrawer.class;
    Class elementClass = e.getClass();
    if (elementClass.getSimpleName().equalsIgnoreCase("GdsPath")) {
      return GdsPathDrawer.class;
    }
    if (elementClass.getSimpleName().equalsIgnoreCase("GdsSref")) {
      return GdsSrefDrawer.class;
    }
    if (elementClass.getSimpleName().equalsIgnoreCase("GdsAref")) {
      return GdsArefDrawer.class;
    }
    return drawerClass;
  }
}

class GdsPathDrawer extends GdsElementDrawer<GdsPath> {

  private GeneralPath pathCenter;

  GdsPathDrawer() {
    super();
  }

  @Override
  public void strokeFrameOn(Graphics2D g) {
    Shape s;
    AffineTransform tx = getViewPortTransform();
    if (isVisiblePathCenter()) {
      s = getPathCenterShape();
      if (tx != null) {
        s = tx.createTransformedShape(s);
      }
      g.draw(s);
    }
    if (isVisiblePathOutline()) {
      s = getPathOutlineShape();
      if (tx != null) {
        s = tx.createTransformedShape(s);
      }
      g.draw(s);
    }
  }

  private Shape getPathCenterShape() {
    if (pathCenter == null) {
      pathCenter = new GeneralPath();
      addPoints(pathCenter, element.getVertices());
    }
    return pathCenter;
  }

  private Shape getPathOutlineShape() {
    if (_framePath == null) {
      _framePath = new GeneralPath();
      addPoints(_framePath, element.outlinePoints());
    }    
    return _framePath;
  }

  private boolean isVisiblePathCenter() {
    // TODO: get from Masks environment
    return false;
  }

  private boolean isVisiblePathOutline() {
    // TODO: get from Masks environment
    return true;
  }

  @Override
  public Paint getPaint() {
    // FIXME:
    return getFrameColor();
  }

  @Override
  public boolean canPaint() {
    // FIXME:
    return false;
  }
}

class GdsBoundaryDrawer extends GdsElementDrawer<GdsBoundary> {

  GdsBoundaryDrawer() {
    super();
  }
}

class GdsSrefDrawer extends GdsElementDrawer<GdsSref> {

  GdsSrefDrawer() {
    super();
  }

  @Override
  public Color getFrameColor() {
    return Color.LIGHT_GRAY;
  }

  @Override
  public void drawOn(Graphics2D g) {
    if (element.getReferenceStructure() == null) {
      return;
    }
    view.getViewPort().pushTransform(element.getTransform());
    view.drawElements(g, element.getReferenceStructure().getElements());
    view.getViewPort().popTransform();
  }
}

class GdsArefDrawer extends GdsElementDrawer<GdsAref> {

  GdsArefDrawer() {
    super();
  }

  @Override
  public void drawOn(Graphics2D g) {
    if (element.getReferenceStructure() == null) {
      return;
    }

    view.getViewPort().pushTransform(element.getTransform());
    for (AffineTransform t : element.getOffsetTransforms()) {
      view.getViewPort().pushTransform(t);
      view.drawElements(g, element.getReferenceStructure().getElements());
      view.getViewPort().popTransform();
    }
    view.getViewPort().popTransform();
  }
}