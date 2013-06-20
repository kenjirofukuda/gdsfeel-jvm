/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.elements.GdsElement;
import com.gdsfeel.fx.ViewPort;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class StructureCanvasPane extends StructureBasePane {

  private static Log log = LogFactory.getLog(StructureCanvasPane.class);
  private ObjectProperty<ViewPort> viewPort;
  private Canvas canvas;

  public StructureCanvasPane() {
    super();
    viewPort = new SimpleObjectProperty<>(this, "viewPort");
    Group root = new Group();
    canvas = new Canvas();
    root.getChildren().add(canvas);
    canvas.widthProperty().bind(this.widthProperty());
    canvas.heightProperty().bind(this.heightProperty());
    ChangeListener<Number> cl = new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov,
                          Number t, Number t1) {
        log.debug("ov = " + ov);
        log.debug("oldValue = " + t);
        log.debug("newValue = " + t);
        handlePaneResized();
      }
    };
    this.widthProperty().addListener(cl);
    this.heightProperty().addListener(cl);
    getChildren().add(root);
    this.setOnScroll(new EventHandler<ScrollEvent>() {
      @Override
      public void handle(ScrollEvent event) {
        handleScrollEvent(event);
      }
    });
  }

  private void handleScrollEvent(ScrollEvent event) {
    double zoomBase = 1.0;
    double zoomUnit = 0.02;
    try {
      AffineTransform tx = getViewPort().getTransform().createInverse();
      Point2D p = tx.transform(new Point2D.Double(event.getX(), event.getY()),
                               null);
      getViewPort().setCenter(p.getX(), p.getY());
      getViewPort().setPortCenter(event.getX(), event.getY());
      getViewPort().zoom(zoomBase + (zoomUnit * event.getDeltaY()));
      repaint();
    }
    catch (NoninvertibleTransformException ex) {
      log.error(ex);
    }
  }

  public ViewPort getViewPort() {
    return viewPort.get();
  }

  private void handlePaneResized() {
    if (getViewPort() != null) {
//    // if use non fx version ViewPort set port size manually.
//      getViewPort().setPortSize(getWidth(), getHeight());
      getViewPort().fit();
    }
    repaint();
  }

  @Override
  protected void clear() {
    log.debug(this);
    getViewPort().portWidthProperty().unbind();
    getViewPort().portHeightProperty().unbind();
    viewPort.set(null);
  }

  @Override
  protected void loadElements() {
    log.debug(this);
    viewPort.set(new ViewPort(getStructure()));
    getViewPort().portWidthProperty().bind(widthProperty());
    getViewPort().portHeightProperty().bind(heightProperty());
//    // if use non fx version ViewPort set port size manually.
//    getViewPort().setPortSize(getWidth(), getHeight());
    getViewPort().fit();
    repaint();
  }

//  // if use non fx version ViewPort set port size manually.
//  private void fit() {
//    getViewPort().resetPortCenter();
//    Rectangle2D r = structure.get().getBoundingBox2();
//    getViewPort().setBounds(r.getMinX(), r.getMinY(),
//                            r.getWidth(), r.getHeight());
//  }
  /**
   *
   */
  private void repaint() {
    GraphicsContext g = canvas.getGraphicsContext2D();
    g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    if (structure.get() == null) {
      return;
    }
    g.setFill(Color.BLACK);
    g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    g.setLineWidth(1.0);
    drawElements(g, displayElements());
  }

  protected Collection<GdsElement> displayElements() {
    return structure.get().getElements();
  }

  private void basicDrawElements(GraphicsContext gc,
                                 Collection<GdsElement> elements) {
    for (GdsElement e : elements) {
      drawElement(gc, e);
    }
  }

  private void drawElement(GraphicsContext g, GdsElement e) {
    GdsElementDrawer drawer = (GdsElementDrawer) e.getRuntimeProperty("drawer");
    if (drawer == null) {
      Class<? extends GdsElementDrawer> drawerClass =
              GdsElementDrawer.drawerClassForElement(e);
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

  public void drawElements(GraphicsContext g,
                           Collection<GdsElement> elements) {
    ArrayList<GdsElement> primitives = new ArrayList<>();
    ArrayList<GdsElement> references = new ArrayList<>();
    GdsElement.splitPlimitivesAndReferences(elements, primitives, references);
    basicDrawElements(g, primitives);
    basicDrawElements(g, references);
  }
}
