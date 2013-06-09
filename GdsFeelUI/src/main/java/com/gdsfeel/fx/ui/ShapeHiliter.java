/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

/**
 *
 * @author kenjiro
 */
public class ShapeHiliter {

  private Shape shape;
  private Paint strokeColor;

  public ShapeHiliter(final Shape shape) {
    this.shape = shape;

    shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent t) {
        strokeColor = shape.getStroke();
        shape.setStroke(Color.WHITE);
      }
    });

    shape.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent t) {
        shape.setStroke(strokeColor);
      }
    });

  }
}
