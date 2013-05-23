/*
 * GdsFeelViewerApp.java
 */
package com.gdsfeel.ui.swing;

import com.gdsfeel.Config;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class GdsFeelViewerApp extends SingleFrameApplication {

  private static Log log = LogFactory.getLog(GdsFeelViewerApp.class);

  /**
   * At startup create and show the main frame of the application.
   */
  @Override
  protected void startup() {
    show(new GdsFeelViewerView(this));
  }

  /**
   * This method is to initialize the specified window by injecting resources.
   * Windows shown in our application come fully initialized from the GUI
   * builder, so this additional configuration is not needed.
   */
  @Override
  protected void configureWindow(java.awt.Window root) {
  }

  /**
   * A convenient static getter for the application instance.
   * @return the instance of GdsFeelViewerApp
   */
  public static GdsFeelViewerApp getApplication() {
    return Application.getInstance(GdsFeelViewerApp.class);
  }


  private static void turnSystemLook() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException ex) {
     log.warn(ex);
    }
    catch (InstantiationException ex) {
     log.warn(ex);
    }
    catch (IllegalAccessException ex) {
     log.warn(ex);
    }
    catch (UnsupportedLookAndFeelException ex) {
     log.warn(ex);
    }
  }


  private static File waitUntilSelectGdsFolder() {
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(false);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle("Choose Squeak Project folder named: GdsFeel");

    for (;;) {
      int reply = chooser.showOpenDialog(null);
      if (reply == JFileChooser.CANCEL_OPTION) {
        return null;
      }
      if (chooser.getSelectedFile().getName().equals("GdsFeel")) {
        break;
      }
      else {
        chooser.setDialogTitle("Select only folder named: GdsFeel");
      }
    }
    return chooser.getSelectedFile();
  }


  private static void resetGdsFolder() {
    File gdsfeelFolder = waitUntilSelectGdsFolder();
    if (gdsfeelFolder == null) {
      System.exit(1);
    }
    Config.setProjectFolder(gdsfeelFolder);
  }
  
  /**
   * Main method launching the application.
   */
  public static void main(String[] args) {
    turnSystemLook();
    if (Config.pathToSmalltalkProject().isEmpty()) {
      resetGdsFolder();
    }
    if (! Config.getProjectFolder().isDirectory()) {
      resetGdsFolder();
    }
    launch(GdsFeelViewerApp.class, args);
  }
  
}
