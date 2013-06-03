/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author kenjiro
 */
public class NodeDragger {

  private double initX;
  private double initY;
  private Node dragNode;

  public NodeDragger(final Node dragNode) {
    this.dragNode = dragNode;

    dragNode.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        initX = me.getScreenX() - dragNode.getLayoutX();
        initY = me.getScreenY() - dragNode.getLayoutY();
      }
    });

    //when screen is dragged, translate it accordingly
    dragNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        dragNode.setLayoutX(me.getScreenX() - initX);
        dragNode.setLayoutY(me.getScreenY() - initY);
      }
    });

  }
}
