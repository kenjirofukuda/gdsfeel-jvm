/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.fx.container.GdsItem;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;

/**
 *
 * @author kenjiro
 */
public class GdsItemPane extends StackPane {

  TextField angle;
  GdsItem item;

  public GdsItemPane() {
    item = new GdsItem();
    initialize();
  }

  private void initialize() {
    angle = TextFieldBuilder.create().id("angle").build();
    Label l = LabelBuilder.create().text("angle:").labelFor(angle).build();
    HBox box = HBoxBuilder.create().build();
    box.getChildren().addAll(l, angle);
    getChildren().addAll(box);
    angle.textProperty().bind(item.magProperty().asString());
  }
}
