/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.util;

/**
 *
 * @author kenjiro
 */
public class Conv {

  public static javafx.scene.paint.Color fromSwing(
          java.awt.Color swingColor) {
    float[] comp = swingColor.getColorComponents(null);
    return javafx.scene.paint.Color.color(comp[0], comp[1], comp[2]);
  }

  public static javafx.scene.shape.Rectangle shapeFromSwing(
          java.awt.geom.Rectangle2D r) {
    return new javafx.scene.shape.Rectangle(
            r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }
}
