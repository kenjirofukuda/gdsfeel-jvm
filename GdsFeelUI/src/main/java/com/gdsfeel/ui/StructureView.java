/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui;

import com.gdsfeel.Structure;
import com.gdsfeel.ViewPort;
import com.gdsfeel.elements.GdsElement;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
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
    ArrayList<GdsElement> primitives = new ArrayList<>();
    ArrayList<GdsElement> references = new ArrayList<>();
    GdsElement.splitPlimitivesAndReferences(elements, primitives, references);
    basicDrawElements(g, primitives.toArray(new GdsElement[0]));
    basicDrawElements(g, references.toArray(new GdsElement[0]));
  }

  private void basicDrawElements(Graphics2D g, GdsElement[] elements) {
    for (GdsElement e : elements) {
      g.setColor(Color.WHITE);
      drawElement(g, e);
    }
  }

  private void drawElement(Graphics2D g, GdsElement e) {
    GdsElementDrawer drawer = (GdsElementDrawer) e.getRuntimeProperty("drawer");
    if (drawer == null) {
      Class<? extends GdsElementDrawer> drawerClass = GdsElementDrawer.drawerClassForElement(e);
      try {
        drawer = drawerClass.newInstance();
        drawer.initWith(e, this);
        e.setRuntimeProperty("drawer", drawer);
      }
      catch (InstantiationException | IllegalAccessException ex) {
        log.error(ex);
      }
    }
    drawer.fullDrawOn(g);
  }

  protected GdsElement[] displayElements() {
    return structure.getElementArray();
  }

//  public AffineTransform transform() {
//    return viewPort.fittingTransform();
//  }
  @Override
  public void componentResized(ComponentEvent ce) {
    if (viewPort != null) {
      viewPort.setPortSize(getSize());
      viewPort.fit();
      repaint();
    }
  }

  @Override
  public void componentMoved(ComponentEvent ce) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void componentShown(ComponentEvent ce) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
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
}
