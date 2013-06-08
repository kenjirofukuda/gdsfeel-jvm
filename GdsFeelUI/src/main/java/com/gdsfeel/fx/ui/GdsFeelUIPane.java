/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.Library;
import com.gdsfeel.Station;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
  private BorderPane border;
  private MenuBar menuBar;
  private SplitPane splitPane;
  private ListView<String> librariesListView;
  private ListView<String> structuewsListView;
  private StructureBasePane structurePane;
  private Stage itemStage;
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
//    this.setStyle("-fx-background-color: gray");
    KindsPane kindsPane = new KindsPane();
    ToolBar tb = new ToolBar(kindsPane, new Separator());
    tb.setOrientation(Orientation.VERTICAL);
    border.setLeft(tb);
  }

  private void addMenuBar() {
    Menu file = new Menu("File");
    Menu view = new Menu("View");
    menuBar.getMenus().addAll(file, view);
    MenuItem newLibrary = new MenuItem("New Library...");
    MenuItem quit = new MenuItem("Quit");
    quit.setOnAction(new EventHandler<ActionEvent>() {
      @Override
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
    border.setTop(menuBar);
    MenuItem showItem = new MenuItem("show item");
    view.getItems().add(showItem);
    showItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent t) {
        if (itemStage == null) {
          itemStage = newWindowFromFXML("GdsItemPane.fxml");
          itemStage.setResizable(false);
        }
        itemStage.show();
      }
    });
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
      Logger.getLogger(GdsFeelUIPane.class.getName()).log(Level.SEVERE, null, ex);
    }
    return stage;
  }

  private boolean isUseSceneGraph() {
    return false;
  }

  private void addSplitPane() {
    if (isUseSceneGraph()) {
      structurePane = new StructureSceneGraphPane();
    }
    else {
      structurePane = new StructureCanvasPane();
    }
    HBox viewControls = createViewControls();
    VBox structureArea = new VBox();
    VBox.setVgrow(structurePane, Priority.ALWAYS);
    structureArea.getChildren().addAll(viewControls, structurePane);
    splitPane.getItems().addAll(createBrowserView(), structureArea);
    splitPane.setDividerPositions(0.3f);
  }

  private HBox createViewControls() {
    HBox box = new HBox();
    Slider viewScale = new Slider();
    viewScale.setMin(0.1);
    viewScale.setMax(10.0);
    viewScale.setValue(1.0);

    structurePane.viewScaleProperty().bindBidirectional(viewScale.valueProperty());
    structurePane.viewScaleProperty().bindBidirectional(viewScale.valueProperty());
    box.getChildren().add(viewScale);
    return box;
  }

  private HBox createBrowserView() {
    HBox box = new HBox();
    librariesListView = new ListView();
//    librariesListView.setPrefWidth(60);
    structuewsListView = new ListView();
//    structuewsListView.setPrefWidth(60);
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
    List<String> names = new ArrayList<>();
    final GdsFeelUIPane top = this;
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
}
