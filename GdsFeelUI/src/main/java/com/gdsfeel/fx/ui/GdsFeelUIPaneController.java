/**
 **/
package com.gdsfeel.fx.ui;

import com.gdsfeel.Library;
import com.gdsfeel.Station;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GdsFeelUIPaneController
        implements Initializable {

  private static Log log = LogFactory.getLog(GdsFeelUIPaneController.class);
  @FXML //  fx:id="border"
  private BorderPane border; // Value injected by FXMLLoader
  @FXML //  fx:id="librariesListView"
  private ListView<String> librariesListView; // Value injected by FXMLLoader
  @FXML //  fx:id="mainStage"
  private StackPane mainStage; // Value injected by FXMLLoader
  @FXML //  fx:id="menuBar"
  private MenuBar menuBar; // Value injected by FXMLLoader
  @FXML //  fx:id="structuewsListView"
  private ListView<String> structuewsListView; // Value injected by FXMLLoader
  // own properties
  private SimpleStringProperty title = new SimpleStringProperty("title");
  private StructureBasePane structurePane;
  private Stage itemStage;
  private Station station;

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    assert border != null : "fx:id=\"border\" was not injected: check your FXML file 'GdsFeelUIPane.fxml'.";
    assert librariesListView != null : "fx:id=\"librariesListView\" was not injected: check your FXML file 'GdsFeelUIPane.fxml'.";
    assert mainStage != null : "fx:id=\"mainStage\" was not injected: check your FXML file 'GdsFeelUIPane.fxml'.";
    assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'GdsFeelUIPane.fxml'.";
    assert structuewsListView != null : "fx:id=\"structuewsListView\" was not injected: check your FXML file 'GdsFeelUIPane.fxml'.";

    // initialize your logic here: all @FXML variables will have been injected
    if (isUseSceneGraph()) {
      structurePane = new StructureSceneGraphPane();
    }
    else {
      structurePane = new StructureCanvasPane();
    }
    mainStage.getChildren().add(structurePane);
  }

  @FXML
  public void handleQuit(ActionEvent t) {
    log.debug("Quit from menu");
    confirmQuit(t);
    Platform.exit();
  }

  @FXML
  public void handleShowItem(ActionEvent t) {
    if (itemStage == null) {
      itemStage = newWindowFromFXML("GdsItemPane.fxml");
      itemStage.setResizable(false);
    }
    itemStage.show();
  }

  private boolean isUseSceneGraph() {
    return false;
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

  private Stage newWindowFromFXML(String resource) {
    log.info("loading ... " + resource);
    Stage stage = new Stage();
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource(resource));
      stage.setScene(new Scene(root));
      stage.centerOnScreen();
    }
    catch (IOException ex) {
      itemStage = null;
      log.error(ex);
    }
    return stage;
  }

  private void fillBrowserView() {
    Validate.notNull(station);
    List<String> names = new ArrayList<>();
    final GdsFeelUIPaneController top = this;
    for (Library l : station.getLibraries()) {
      names.add(l.getName());
    }
    librariesListView.setItems(
            FXCollections.observableArrayList(names));
    librariesListView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
      @Override
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
      @Override
      public void changed(ObservableValue<? extends String> ov, String t, String t1) {
        log.debug(t1);
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
}
