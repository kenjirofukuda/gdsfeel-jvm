/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class GdsFeelApplication extends Application {

  private static Log log = LogFactory.getLog(GdsFeelApplication.class);
  GdsFeelUIPane uiPane;

  @Override
  public void start(Stage primaryStage) {

    uiPane = new GdsFeelUIPane();
    uiPane.init();
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    Scene scene = new Scene(uiPane,
                            primaryScreenBounds.getWidth() * 0.8,
                            primaryScreenBounds.getHeight() * 0.8);

    primaryStage.setTitle("GdsFeel");
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      public void handle(WindowEvent t) {
        uiPane.confirmQuit(t);
      }
    });
    primaryStage.titleProperty().bind(uiPane.titleProperty());
    primaryStage.show();
  }

  @Override
  public void init() throws Exception {
    log.debug(this.getParameters().getNamed());
    log.debug(this.getParameters().getRaw());
    log.debug(this.getParameters().getUnnamed());
  }

  @Override
  public void stop() throws Exception {
    log.debug("Application stop");
    uiPane.stop();
  }

  /**
   * The main() method is ignored in correctly deployed JavaFX application.
   * main() serves only as fallback in case the application can not be launched
   * through deployment artifacts, e.g., in IDEs with limited FX support.
   * NetBeans ignores main().
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    System.setProperty("com.gdsfeel.useFxProperty", "true");
    launch(args);
  }
}
