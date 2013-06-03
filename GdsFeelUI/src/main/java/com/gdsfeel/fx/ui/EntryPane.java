/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 *
 * @author kenjiro
 */
public class EntryPane extends StackPane {

  private CheckBox selectableCB = new CheckBox();
  private CheckBox visibleCB = new CheckBox();
  private Label label = new Label();

  public EntryPane() {
    super();
    init();
  }

  public CheckBox getSelectableCheckBox() {
    return selectableCB;
  }

  public CheckBox getVisibleCheckBox() {
    return visibleCB;
  }

  public Label getLabel() {
    return label;
  }

  private void init() {
    selectableCB.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent t) {
        selectableClicked(t);
      }
    });
    visibleCB.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent t) {
        visibleClicked(t);
      }
    });
    HBox hBox = new HBox();
    hBox.getChildren().add(label);
    hBox.getChildren().add(visibleCB);
    hBox.getChildren().add(selectableCB);
    getChildren().add(hBox);
  }

  public void selectableClicked(ActionEvent t) {
  }

  public void visibleClicked(ActionEvent t) {
  }
}
