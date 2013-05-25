/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author kenjiro
 */
public class GdsFeelUIPane extends StackPane {

  public GdsFeelUIPane() {
    super();
    initComponents();
  }

  private void initComponents() {
    this.setStyle("-fx-background-color: gray");
    BorderPane border = new BorderPane();

    KindsPane kindsPane = new KindsPane();
    ToolBar tb = new ToolBar(kindsPane, new Separator());
    tb.setMinWidth(60);
    tb.setOrientation(Orientation.VERTICAL);
    border.setLeft(tb);
    getChildren().add(border);
  }
}
