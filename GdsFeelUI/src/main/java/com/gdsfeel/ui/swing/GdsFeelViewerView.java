/*
 * GdsFeelViewerView.java
 */
package com.gdsfeel.ui.swing;

import com.gdsfeel.Library;
import com.gdsfeel.Station;
import java.util.EventObject;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application.ExitListener;

/**
 * The application's main frame.
 */
public class GdsFeelViewerView extends FrameView {

  private static Log log = LogFactory.getLog(GdsFeelViewerView.class);
  private Station _station;
  
  public GdsFeelViewerView(SingleFrameApplication app) {
    super(app);
    initComponents();
    postInitComponenets();
    app.addExitListener( new ExitListener() {

      @Override
      public boolean canExit(EventObject arg0) {
        return true;
      }

      @Override
      public void willExit(EventObject arg0) {
        if (_station != null) {
          _station.tearDown();
        }
      }
    });

    // status bar initialization - message timeout, idle icon and busy animation, etc
    ResourceMap resourceMap = getResourceMap();
    int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
    messageTimer = new Timer(messageTimeout, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        statusMessageLabel.setText("");
      }
    });
    messageTimer.setRepeats(false);
    int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
    for (int i = 0; i < busyIcons.length; i++) {
      busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
    }
    busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
        statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
      }
    });
    idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
    statusAnimationLabel.setIcon(idleIcon);
    progressBar.setVisible(false);

    // connecting action tasks to status bar via TaskMonitor
    TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
    taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

      @Override
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if ("started".equals(propertyName)) {
          if (!busyIconTimer.isRunning()) {
            statusAnimationLabel.setIcon(busyIcons[0]);
            busyIconIndex = 0;
            busyIconTimer.start();
          }
          progressBar.setVisible(true);
          progressBar.setIndeterminate(true);
        }
        else if ("done".equals(propertyName)) {
          busyIconTimer.stop();
          statusAnimationLabel.setIcon(idleIcon);
          progressBar.setVisible(false);
          progressBar.setValue(0);
        }
        else if ("message".equals(propertyName)) {
          String text = (String) (evt.getNewValue());
          statusMessageLabel.setText((text == null) ? "" : text);
          messageTimer.restart();
        }
        else if ("progress".equals(propertyName)) {
          int value = (Integer) (evt.getNewValue());
          progressBar.setVisible(true);
          progressBar.setIndeterminate(false);
          progressBar.setValue(value);
        }
      }
    });
  }


  private void postInitComponenets() {
    _station = new Station();
    _station.setup();
    libraryListView.setModel(new LibraryListModel(_station));
    _structureView = new StructureView();
    jPanel2.add(_structureView);
  }

  
  @Action
  public void showAboutBox() {
    if (aboutBox == null) {
      JFrame mainFrame = GdsFeelViewerApp.getApplication().getMainFrame();
      aboutBox = new GdsFeelViewerAboutBox(mainFrame);
      aboutBox.setLocationRelativeTo(mainFrame);
    }
    GdsFeelViewerApp.getApplication().show(aboutBox);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    mainPanel = new javax.swing.JPanel();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    libraryListView = new javax.swing.JList();
    jScrollPane2 = new javax.swing.JScrollPane();
    structureListView = new javax.swing.JList();
    jPanel2 = new javax.swing.JPanel();
    menuBar = new javax.swing.JMenuBar();
    javax.swing.JMenu fileMenu = new javax.swing.JMenu();
    javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
    javax.swing.JMenu helpMenu = new javax.swing.JMenu();
    javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
    statusPanel = new javax.swing.JPanel();
    javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
    statusMessageLabel = new javax.swing.JLabel();
    statusAnimationLabel = new javax.swing.JLabel();
    progressBar = new javax.swing.JProgressBar();

    mainPanel.setName("mainPanel"); // NOI18N

    jSplitPane1.setBorder(null);
    jSplitPane1.setDividerLocation(200);
    jSplitPane1.setDividerSize(10);
    jSplitPane1.setInheritsPopupMenu(true);
    jSplitPane1.setName("jSplitPane1"); // NOI18N
    jSplitPane1.setOneTouchExpandable(true);

    jPanel1.setName("jPanel1"); // NOI18N
    jPanel1.setLayout(new java.awt.GridLayout(1, 0));

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    libraryListView.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    libraryListView.setName("libraryListView"); // NOI18N
    libraryListView.setValueIsAdjusting(true);
    libraryListView.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        libraryListViewValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(libraryListView);

    jPanel1.add(jScrollPane1);

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    structureListView.setName("structureListView"); // NOI18N
    structureListView.setValueIsAdjusting(true);
    structureListView.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        structureListViewValueChanged(evt);
      }
    });
    jScrollPane2.setViewportView(structureListView);

    jPanel1.add(jScrollPane2);

    jSplitPane1.setLeftComponent(jPanel1);

    jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    jPanel2.setName("jPanel2"); // NOI18N
    jPanel2.setLayout(new java.awt.BorderLayout());
    jSplitPane1.setRightComponent(jPanel2);

    javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        .addContainerGap())
    );
    mainPanelLayout.setVerticalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
        .addContainerGap())
    );

    menuBar.setName("menuBar"); // NOI18N

    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.gdsfeel.ui.swing.GdsFeelViewerApp.class).getContext().getResourceMap(GdsFeelViewerView.class);
    fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
    fileMenu.setName("fileMenu"); // NOI18N

    javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.gdsfeel.ui.swing.GdsFeelViewerApp.class).getContext().getActionMap(GdsFeelViewerView.class, this);
    exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
    exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
    exitMenuItem.setName("exitMenuItem"); // NOI18N
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
    helpMenu.setName("helpMenu"); // NOI18N

    aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
    aboutMenuItem.setName("aboutMenuItem"); // NOI18N
    helpMenu.add(aboutMenuItem);

    menuBar.add(helpMenu);

    statusPanel.setName("statusPanel"); // NOI18N

    statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

    statusMessageLabel.setName("statusMessageLabel"); // NOI18N

    statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

    progressBar.setName("progressBar"); // NOI18N

    javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
    statusPanel.setLayout(statusPanelLayout);
    statusPanelLayout.setHorizontalGroup(
      statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
      .addGroup(statusPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(statusMessageLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 445, Short.MAX_VALUE)
        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(statusAnimationLabel)
        .addContainerGap())
    );
    statusPanelLayout.setVerticalGroup(
      statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(statusPanelLayout.createSequentialGroup()
        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(statusMessageLabel)
          .addComponent(statusAnimationLabel)
          .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(3, 3, 3))
    );

    setComponent(mainPanel);
    setMenuBar(menuBar);
    setStatusBar(statusPanel);
  }// </editor-fold>//GEN-END:initComponents

  private void libraryListViewValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_libraryListViewValueChanged
    librarySelectionChanged(evt);
  }//GEN-LAST:event_libraryListViewValueChanged

  private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
    exitApplication(evt);
  }//GEN-LAST:event_exitMenuItemActionPerformed

  private void structureListViewValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_structureListViewValueChanged
    structureSelectionChanged(evt);
  }//GEN-LAST:event_structureListViewValueChanged

  private void exitApplication(ActionEvent evt) {
    _station.tearDown();
    _station = null;
  }

  private void structureSelectionChanged(ListSelectionEvent evt) {
    if (evt.getValueIsAdjusting()) return;
    int newSelectedIndex = structureListView.getSelectedIndex();
    log.debug(newSelectedIndex);
    String strucname = (String) structureListView.getSelectedValue();
    if (StringUtils.isEmpty(strucname)) {
      return;
    }
    _station.activateStructureNamed(strucname);
     if (_station.getStructure() == null) {
      log.error("structure not found: " + strucname);
      return;
    }
    _structureView.setStructure(_station.getStructure());
  }

  private void librarySelectionChanged(ListSelectionEvent evt) {
    if (evt.getValueIsAdjusting()) return;
    int newSelectedIndex = libraryListView.getSelectedIndex();
    log.debug(newSelectedIndex);
    String libname = (String) libraryListView.getSelectedValue();
    if (StringUtils.isEmpty(libname)) {
      return;
    }
    _station.activateLibraryNamed(libname);
    if (_station.getLibrary() == null) {
      log.error("library not found: " + libname);
      return;
    }
    structureListView.setModel(new StructureListModel(_station.getLibrary()));
  }

  private StructureView _structureView;

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JList libraryListView;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JProgressBar progressBar;
  private javax.swing.JLabel statusAnimationLabel;
  private javax.swing.JLabel statusMessageLabel;
  private javax.swing.JPanel statusPanel;
  private javax.swing.JList structureListView;
  // End of variables declaration//GEN-END:variables
  private final Timer messageTimer;
  private final Timer busyIconTimer;
  private final Icon idleIcon;
  private final Icon[] busyIcons = new Icon[15];
  private int busyIconIndex = 0;
  private JDialog aboutBox;
}

class LibraryListModel extends AbstractListModel {

  private Station _station;

  public LibraryListModel(Station station) {
    _station = station;
  }

  @Override
  public int getSize() {
    return _station.getLibraries().size();
  }

  @Override
  public Object getElementAt(int index) {
    return _station.getLibraries().get(index).getName();
  }
}


class StructureListModel extends AbstractListModel {

  private Library _library;
  private String[] _names;

  public StructureListModel(Library library) {
    _library = library;
    _names = _library.getStructureNames();
  }

  @Override
  public int getSize() {
    return _names.length;
  }

  
  @Override
  public Object getElementAt(int index) {
    return _names[index];
  }

}
