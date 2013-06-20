/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ide;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "com.gdsfeel.ide.OpenLibraryAction")
@ActionRegistration(
        displayName = "#CTL_OpenLibraryAction")
@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_OpenLibraryAction=Open Library...")
public final class OpenLibraryAction implements ActionListener {

  @Override
  public void actionPerformed(ActionEvent e) {
    System.out.println(e);
    File home = new File(System.getProperty("user.home"));
    File toAdd = new FileChooserBuilder("user-dir")
            .setTitle("Open File")
            .setDefaultWorkingDirectory(home)
            .setApproveText("Open")
            .setFileHiding(true)
            .setFilesOnly(true)
            .addFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        return FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("DB");
      }

      @Override
      public String getDescription() {
        return "GdsFeel Database (*.DB)";
      }
    }).showOpenDialog();

    if (toAdd != null) {
      System.out.println(toAdd);
    }
  }
}
