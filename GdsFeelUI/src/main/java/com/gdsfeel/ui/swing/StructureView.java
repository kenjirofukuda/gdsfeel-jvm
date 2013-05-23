/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui.swing;

import com.gdsfeel.Structure;
import com.gdsfeel.ViewPort;
import com.gdsfeel.elements.GdsAref;
import com.gdsfeel.elements.GdsElement;
import com.gdsfeel.elements.GdsPrimitiveElement;
import com.gdsfeel.elements.GdsReferenceElement;
import com.gdsfeel.elements.GdsSref;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author kenjiro
 */
public class StructureView extends JPanel implements ComponentListener {

  private static Log log = LogFactory.getLog(StructureView.class);
  private ViewPort viewPort;
  private Structure structure;

  public StructureView() {
    super();
  }

  public void setStructure(Structure structure) {
    Validate.notNull(structure);
    this.structure = structure;
    if (structure == null) {
      return;
    }
    addComponentListener(this);
    this.structure.load();
    viewPort = new ViewPort(structure);
    viewPort.setPortSize(getSize());
    viewPort.fit();
    this.repaint();
  }

  public ViewPort getViewPort() {
    return viewPort;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2 = (Graphics2D) g;
    fillBackground(g2);
    if (structure == null) {
      return;
    }
    drawElements(g2, displayElements());
  }

  public void drawElements(Graphics2D g, GdsElement[] elements) {
    ArrayList<GdsElement> primitives = new ArrayList<GdsElement>();
    ArrayList<GdsElement> references = new ArrayList<GdsElement>();
    splitPlimitivesAndReferences(elements, primitives, references);
    basicDrawElements(g, primitives.toArray(new GdsElement[0]));
    basicDrawElements(g, references.toArray(new GdsElement[0]));
  }

  private void splitPlimitivesAndReferences(GdsElement[] elements,
          ArrayList<GdsElement> primitives, ArrayList<GdsElement> references) {
    for (GdsElement e : elements) {
      if (e instanceof GdsSref || e instanceof GdsAref) {
        references.add(e);
      }
      else {
        primitives.add(e);
      }
    }
  }

  private void basicDrawElements(Graphics2D g, GdsElement[] elements) {
    for (GdsElement e : elements) {
      g.setColor(Color.WHITE);
      drawElement(g, e);
    }
  }

  private void drawElement(Graphics2D g, GdsElement e) {
    GdsElementDrawer drawer;
    Class<? extends GdsElementDrawer> drawerClass = GdsElementDrawer.drawerClassForElement(e);
    try {
      drawer = drawerClass.newInstance();
      drawer.initWith(e, this);
      drawer.fullDrawOn(g);
    }
    catch (InstantiationException ex) {
      log.error(ex);
    }
    catch (IllegalAccessException ex) {
      log.error(ex);
    }
  }

  protected GdsElement[] displayElements() {
    return structure.getElements();
  }

//  public AffineTransform transform() {
//    return viewPort.fittingTransform();
//  }
  public void componentResized(ComponentEvent ce) {
    if (viewPort != null) {
      viewPort.setPortSize(getSize());
      viewPort.fit();
      repaint();
    }
  }

  public void componentMoved(ComponentEvent ce) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void componentShown(ComponentEvent ce) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void componentHidden(ComponentEvent ce) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  private void fillBackground(Graphics2D g2) {
    g2.setColor(getBackgroundColor());
    g2.fillRect(0, 0, getWidth(), getHeight());
  }
  
  private Color getBackgroundColor() {
    return Color.black;
  }
  
  private Color getPenColor() {
    return Color.lightGray;
  }
  
  private AffineTransform fitTransform() {
    AffineTransform tx = new AffineTransform();
    Rectangle2D totalBounds = modelBounds();
    if (totalBounds.isEmpty()) {
      return tx;
    }
    tx.concatenate(flipTransform());
    tx.concatenate(toCenterTransform());
    tx.concatenate(fitScaleTransform());
    tx.concatenate(viewToCenterTransform());
    return tx;
  }

  private AffineTransform flipTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(0, this.getHeight());
    tx.scale(1, -1);
    return tx;
  }

  private AffineTransform toCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate((this.getWidth() / 2), (this.getHeight() / 2));
    return tx;
  }

  private AffineTransform viewToCenterTransform() {
    AffineTransform tx = new AffineTransform();
    tx.translate(-modelBounds().getCenterX(), -modelBounds().getCenterY());
    return tx;
  }

  private AffineTransform fitScaleTransform() {
    AffineTransform tx = new AffineTransform();
    Rectangle2D totalBounds = modelBounds();
    if (totalBounds.isEmpty()) {
      return tx;
    }
    double hRatio = this.getWidth() / totalBounds.getWidth() ;
    double vRatio = this.getHeight() / totalBounds.getHeight() ;
    double modelToPixelRatio = Math.min(hRatio,vRatio);
    tx.setToScale(modelToPixelRatio, modelToPixelRatio);
    return tx;
  }

  private Rectangle2D modelBounds() {
    return structure.getBoundingBox();
  }

}
class GdsElementDrawer {

  private static Log log = LogFactory.getLog(GdsElementDrawer.class);
  protected GdsElement element;
  protected StructureView view;

  public void initWith(GdsElement element, StructureView view) {
    this.element = element;
    this.view = view;
  }

  public void fullDrawOn(Graphics2D g) {
    Color savedColor = g.getColor();
    g.setColor(frameColor());
    drawOn(g);
    g.setColor(savedColor);
  }

  public Color frameColor() {
    Color result = Color.white;
    try {
      GdsPrimitiveElement pe = (GdsPrimitiveElement) element;
      result = pe.getStructure().colorForLayerNumber(pe.getLayerNumber());
    }
    catch (ClassCastException ex) {
    }
    return result;
  }

  public void drawOn(Graphics2D g) {
    strokePoints(g, element.outlinePoints(), view.getViewPort().getTransform());
  }

  public void strokePoints(Graphics2D g, Point2D[] points, AffineTransform tx) {
    GeneralPath path = new GeneralPath();
    addPoints(path, points);
    Shape s = path;
    if (tx != null) {
      s = tx.createTransformedShape(path);
    }
    g.draw(s);
  }

  public void strokePoints(Graphics2D g, Point2D[] points) {
    strokePoints(g, points, null);
  }

  private void addPoints(GeneralPath path, Point2D[] points) {
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

  static Class<? extends GdsElementDrawer> drawerClassForElement(GdsElement e) {
    Class<? extends GdsElementDrawer> drawerClass = GdsElementDrawer.class;
    Class elementClass = e.getClass();
    if (elementClass.getSimpleName().equalsIgnoreCase("GdsSref")) {
      return GdsSrefDrawer.class;
    }
    if (elementClass.getSimpleName().equalsIgnoreCase("GdsAref")) {
      return GdsArefDrawer.class;
    }
    return drawerClass;
  }
}

class GdsPathDrawer extends GdsElementDrawer {

  GdsPathDrawer() {
    super();
  }
}

class GdsBoundaryDrawer extends GdsElementDrawer {

  GdsBoundaryDrawer() {
    super();
  }
}

class GdsSrefDrawer extends GdsElementDrawer {

  GdsSrefDrawer() {
    super();
  }

  @Override
  public Color frameColor() {
    return Color.LIGHT_GRAY;
  }

  @Override
  public void drawOn(Graphics2D g) {
    if (element.resolveStructure() == null) {
      return;
    }
    GdsSref e = (GdsSref) element;
    view.getViewPort().pushTransform(e.getTransform());
    view.drawElements(g, e.resolveStructure().getElements());
    view.getViewPort().popTransform();
  }
}

class GdsArefDrawer extends GdsElementDrawer {

  GdsArefDrawer() {
    super();
  }

  @Override
  public void drawOn(Graphics2D g) {
    if (element.resolveStructure() == null) {
      return;
    }
    GdsAref e = (GdsAref) element;

    view.getViewPort().pushTransform(e.getTransform());
    for (AffineTransform t : e.getOffsetTransforms()) {
      view.getViewPort().pushTransform(t);
      view.drawElements(g, e.resolveStructure().getElements());
      view.getViewPort().popTransform();
    }
    view.getViewPort().popTransform();
  }
}
