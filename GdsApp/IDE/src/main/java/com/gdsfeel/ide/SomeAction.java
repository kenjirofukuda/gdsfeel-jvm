/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ide;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "com.gdsfeel.ide.SomeAction")
@ActionRegistration(
        displayName = "#CTL_SomeAction")
@ActionReference(path = "Menu/File", position = 1200)
@Messages("CTL_SomeAction=Open Some")
public final class SomeAction implements ActionListener {

  private final List<OpenCookie> context;

  public SomeAction(List<OpenCookie> context) {
    this.context = context;
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    for (OpenCookie openCookie : context) {
      // TODO use openCookie
    }
  }
}
