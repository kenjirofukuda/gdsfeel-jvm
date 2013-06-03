/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.fx.ui;

import com.gdsfeel.fx.container.GdsItem;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FXML Controller class
 *
 * @author kenjiro
 */
public class GdsItemPaneController implements Initializable {

  private static Log log = LogFactory.getLog(GdsItemPaneController.class);
  @FXML
  GdsItem gdsItem;
  @FXML
  TextField magTF;
  @FXML
  TextField angleTF;
  @FXML
  CheckBox reflectedCB;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    log.info("url = " + url);
    log.info("rb = " + rb);
    gdsItem = new GdsItem();
    installDebugListener(gdsItem);
    installKeyFilter(magTF);
    installKeyFilter(angleTF);


    StringConverter dc = new DoubleStringConverter();

    Bindings.bindBidirectional(magTF.textProperty(), gdsItem.magProperty(), dc);
    Bindings.bindBidirectional(angleTF.textProperty(), gdsItem.angleProperty(), dc);
    reflectedCB.selectedProperty().bindBidirectional(gdsItem.reflectedProperty());
  }

  private void installKeyFilter(TextField tf) {
    tf.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent t) {
        char ar[] = t.getCharacter().toCharArray();
        char ch = ar[t.getCharacter().toCharArray().length - 1];
        if (!((ch >= '0' && ch <= '9') || (ch == '.') || (ch == '-'))) {
          System.out.println("The char you entered is not a number");
          t.consume();
        }
      }
    });

  }

  private void installDebugListener(GdsItem gi) {
    gi.angleProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
        System.out.println("*** CHANGED(angle) ***");
        System.out.println("ov = " + ov);
        System.out.println("t = " + t);
        System.out.println("t1 = " + t1);
      }
    });
    gi.magProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
        System.out.println("*** CHANGED(mag) ***");
        System.out.println("ov = " + ov);
        System.out.println("t = " + t);
        System.out.println("t1 = " + t1);
      }
    });
    gi.reflectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
        System.out.println("*** CHANGED(reflected) ***");
        System.out.println("ov = " + ov);
        System.out.println("t = " + t);
        System.out.println("t1 = " + t1);
      }
    });
  }

  @FXML
  public void angleEnterd(ActionEvent evt) {
    System.out.println(evt);
    angleTF.setText(Double.toString(gdsItem.getAngle()));
  }
}
