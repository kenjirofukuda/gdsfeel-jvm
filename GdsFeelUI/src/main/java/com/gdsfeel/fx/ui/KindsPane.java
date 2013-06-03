/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.Kind;
import com.gdsfeel.Kinds;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author kenjiro
 */
public class KindsPane extends StackPane {

  private List<KindEntryPane> entries;

  protected List<KindEntryPane> getEntries() {
    return entries;
  }

  public KindsPane() {
    super();
    initialize();
  }

  private void initialize() {
    //this.setStyle("-fx-background-color: blue"); 
    VBox vb = new VBox();
    vb.getChildren().add(new KindAllPane(this));
    entries = new ArrayList<KindEntryPane>();
    for (Kind k : Kinds.KEYS) {
      Kind.Entry e = Kinds.getInstance().get(k);
      KindEntryPane entryPane = new KindEntryPane(e);
      entries.add(entryPane);
      vb.getChildren().add(entryPane);
    }
    getChildren().add(vb);
  }
}

class KindEntryPane extends EntryPane {

  private Kind.Entry kindEntry;
  final double BASE_SIZE = 18;

  public KindEntryPane(Kind.Entry kindEntry) {
    super();
    this.kindEntry = kindEntry;
    getLabel().setText(kindEntry.getKind().getAddrev());
    getLabel().setTooltip(new Tooltip(kindEntry.getKind().getDescription()));
    getLabel().setPrefSize(BASE_SIZE + 2, BASE_SIZE);
  }

  @Override
  public void selectableClicked(ActionEvent t) {
    kindEntry.setSelectable(getSelectableCheckBox().isSelected());
  }

  @Override
  public void visibleClicked(ActionEvent t) {
    kindEntry.setVisible(getVisibleCheckBox().isSelected());
  }
}

class KindAllPane extends KindEntryPane {

  KindsPane owner;

  public KindAllPane(KindsPane owner) {
    super(new Kind.Entry(new Kind("--", "change all property")));
    this.owner = owner;
  }

  @Override
  public void selectableClicked(ActionEvent t) {
    for (KindEntryPane p : owner.getEntries()) {
      p.getSelectableCheckBox().setSelected(getSelectableCheckBox().isSelected());
    }
  }

  @Override
  public void visibleClicked(ActionEvent t) {
    for (KindEntryPane p : owner.getEntries()) {
      p.getVisibleCheckBox().setSelected(getVisibleCheckBox().isSelected());
    }
  }
}