/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ui;

import com.gdsfeel.Library;
import com.gdsfeel.Station;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class GdsFeelUIPane extends StackPane {

  private SimpleStringProperty title = new SimpleStringProperty("title");
  private static Log log = LogFactory.getLog(GdsFeelUIPane.class);
  BorderPane border;
  MenuBar menuBar;
  SplitPane splitPane;
  ListView<String> librariesListView;
  ListView<String> structuewsListView;
  StructurePane structurePane;
  private Station station;

  public GdsFeelUIPane() {
    super();
    initComponents();
  }

  /**
   * Get the value of title
   *
   * @return the value of title
   */
  public String getTitle() {
    return title.get();
  }

  /**
   * Set the value of title
   *
   * @param title new value of title
   */
  public void setTitle(String title) {
    this.title.set(title);
  }

  public StringProperty titleProperty() {
    return title;
  }

  private void initComponents() {
    border = new BorderPane();
    menuBar = new MenuBar();
    splitPane = new SplitPane();
    border.setCenter(splitPane);
    getChildren().add(border);
    addLeftBorderPane();
    addMenuBar();
    addSplitPane();
  }

  private void addLeftBorderPane() {
    this.setStyle("-fx-background-color: gray");

    KindsPane kindsPane = new KindsPane();
    ToolBar tb = new ToolBar(kindsPane, new Separator());
    tb.setOrientation(Orientation.VERTICAL);
    border.setLeft(tb);
  }

  private void addMenuBar() {
    Menu file = new Menu("File");
    menuBar.getMenus().add(file);
    MenuItem newLibrary = new MenuItem("New Library...");
    MenuItem quit = new MenuItem("Quit");
    quit.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent t) {
        log.debug("Quit from menu");
        confirmQuit(t);
        Platform.exit();
      }
    });

    file.getItems().addAll(
            newLibrary,
            new SeparatorMenuItem(),
            quit);

    //menuBar.setPrefHeight(20);
    border.setTop(menuBar);
  }

  private void addSplitPane() {
    final StackPane sp1 = new StackPane();
    sp1.getChildren().add(new Button("Button One"));
    final StackPane sp2 = new StackPane();
    sp2.getChildren().add(new Button("Button Two"));
    structurePane = new StructurePane();
    splitPane.getItems().addAll(createBrowserView(), structurePane);
    splitPane.setDividerPositions(0.3f);
  }

  private HBox createBrowserView() {
    HBox box = new HBox();
    librariesListView = new ListView();
    structuewsListView = new ListView();
    box.getChildren().addAll(
            librariesListView, structuewsListView);
    return box;
  }

  public void confirmQuit(Event e) {
    log.debug("confirmQuit(" + e + ")");
  }

  public void init() {
    station = new Station();
    station.setup();

    fillBrowserView();
  }

  public void stop() {
    station.tearDown();
  }

  private void fillBrowserView() {
    Validate.notNull(station);
    List<String> names = new ArrayList<String>();
    final GdsFeelUIPane top = this;
    for (Library l : station.getLibraries()) {
      names.add(l.getName());
    }
    librariesListView.setItems(
            FXCollections.observableArrayList(names));
    librariesListView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
      public void changed(ObservableValue<? extends String> ov, String t, String t1) {
        station.activateLibraryNamed(t1);
        top.setTitle(t1);
        structuewsListView.getItems().clear();
        structuewsListView.setItems(
                FXCollections.observableArrayList(
                station.getLibrary().getStructureNames()));
      }
    });

    structuewsListView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
      public void changed(ObservableValue<? extends String> ov, String t, String t1) {
        log.info(t1);
        if (StringUtils.isEmpty(t1)) {
          structurePane.setStructure(null);
          return;
        }
        station.activateStructureNamed(t1);
        top.setTitle(t1 + " - " + station.getLibrary().getName());
        structurePane.setStructure(station.getStructure());
      }
    });
  }
}
