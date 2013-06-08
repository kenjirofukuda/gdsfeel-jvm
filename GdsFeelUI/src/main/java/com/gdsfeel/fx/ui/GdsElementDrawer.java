/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.elements.GdsAref;
import com.gdsfeel.elements.GdsBoundary;
import com.gdsfeel.elements.GdsElement;
import com.gdsfeel.elements.GdsPath;
import com.gdsfeel.elements.GdsPrimitiveElement;
import com.gdsfeel.elements.GdsSref;
import com.gdsfeel.fx.container.GdsPoints;
import com.gdsfeel.util.Conv;
import java.awt.geom.AffineTransform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class GdsElementDrawer<T extends GdsElement> {

  private static Log log = LogFactory.getLog(GdsElementDrawer.class);
  protected T element;
  protected StructureCanvasPane view;
  private Color frameColor;
  private double[] outlineXY;

  public void initWith(GdsElement element, StructureCanvasPane view) {
    this.element = (T) element;
    this.view = view;
  }

  public final void fullDrawOn(GraphicsContext g) {
    // FIXME:
    drawOn(g);
  }

  public void drawOn(GraphicsContext g) {
    // FIXME:
    g.setStroke(getFrameColor());
    strokeFrameOn(g);
  }

  public Color getFrameColor() {
    if (frameColor == null) {
      frameColor = lookupFrameColor();
    }
    return frameColor;
  }

  private Color lookupFrameColor() {
    Color result = Color.WHITE;
    try {
      GdsPrimitiveElement pe = (GdsPrimitiveElement) element;
      result = Conv.fromSwing(
              GdsPrimitiveElement.lookupFrameColor(pe));
    }
    catch (ClassCastException ex) {
    }
    return result;
  }

  // Overridable
  public void strokeFrameOn(GraphicsContext g) {
    strokePoints(g, element.outlinePoints2(), getViewPortTransform());
  }

  public void strokePoints(GraphicsContext g,
                           GdsPoints points,
                           AffineTransform tx) {
    int numPoints = points.getSize();
    int allocSize = numPoints * 2;
    if (outlineXY == null) {
      outlineXY = new double[allocSize];
      points.flattenXY(outlineXY);
    }
    double[] xy = new double[allocSize];
    tx.transform(outlineXY, 0, xy, 0, numPoints);
    double[] xa = new double[numPoints];
    double[] ya = new double[numPoints];
    for (int i = 0; i < numPoints; i++) {
      int ai = i * 2;
      xa[i] = xy[ai];
      ya[i] = xy[ai + 1];
    }
    g.strokePolyline(xa, ya, numPoints);
  }

  protected AffineTransform getViewPortTransform() {
    return view.getViewPort().getTransform2();
  }

  static Class<? extends GdsElementDrawer> drawerClassForElement(GdsElement e) {
    Class<? extends GdsElementDrawer> drawerClass =
            GdsElementDrawer.class;
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

  GdsPathDrawer() {
    super();
  }

  @Override
  public void strokeFrameOn(GraphicsContext g) {
    AffineTransform tx = getViewPortTransform();
    if (isVisiblePathCenter()) {
      strokePoints(g, element.getVertices2(), tx);
    }
    if (isVisiblePathOutline()) {
      strokePoints(g, element.outlinePoints2(), tx);
    }
  }

  private boolean isVisiblePathCenter() {
    // TODO: get from Masks environment
    return false;
  }

  private boolean isVisiblePathOutline() {
    // TODO: get from Masks environment
    return true;
  }
}

class GdsBoundaryDrawer extends GdsElementDrawer<GdsBoundary> {

  GdsBoundaryDrawer() {
    super();
  }
}

class GdsSrefDrawer extends GdsElementDrawer<GdsSref> {

  private static Log log = LogFactory.getLog(GdsSrefDrawer.class);

  GdsSrefDrawer() {
    super();
  }

  @Override
  public Color getFrameColor() {
    return Color.LIGHTGRAY;
  }

  @Override
  public void drawOn(GraphicsContext g) {
    if (element.getReferenceStructure() == null) {
      return;
    }
    view.getViewPort().pushTransform(element.getTransform());
    view.drawElements(g,
                      element.getReferenceStructure().getElements());
    view.getViewPort().popTransform();
  }
}

class GdsArefDrawer extends GdsElementDrawer<GdsAref> {

  private static Log log = LogFactory.getLog(GdsArefDrawer.class);

  GdsArefDrawer() {
    super();
  }

  @Override
  public void drawOn(GraphicsContext g) {
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
