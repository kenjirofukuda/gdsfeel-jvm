/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ide;

import com.gdsfeel.Structure;
import static com.gdsfeel.ide.Bundle.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author kenjiro
 */
class StructureNode extends AbstractNode {

  private Structure structure;

  StructureNode(Structure obj) {
    super(Children.LEAF, Lookups.singleton(obj));
    this.structure = obj;
    setDisplayName(obj.getName());
  }

  @Override
  public Action[] getActions(boolean context) {
    List<Action> actions = new ArrayList<Action>();
    actions.add(new OpenStructureAction(structure));
    return actions.toArray(new Action[actions.size()]);
  }

  @Override
  public Image getIcon(int type) {
    return ImageUtilities.loadImage(
            "com/gdsfeel/ide/1371273890_stock_graphic-styles-16.png");
  }

  @Override
  public Image getOpenedIcon(int type) {
    return getIcon(type);
  }

  private class OpenStructureAction extends AbstractAction {

    private final Structure structure;

    @Messages("BTN_OpenStructureAction=Open Structure")
    OpenStructureAction(Structure structure) {
      putValue(Action.NAME, BTN_OpenStructureAction());
      this.structure = structure;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TopComponent tc = null;

      for (TopComponent t : TopComponent.getRegistry().getOpened()) {
        if (t instanceof StructureTopComponent) {
          StructureTopComponent stc = (StructureTopComponent) t;
          if (stc.getStructure() == structure) {
            tc = t;
            break;
          }
        }
      }
      if (tc == null) {
        tc = new StructureTopComponent(structure);
//        tc.setIcon(((AbstractNode) e.getSource()).getIcon(0));
        tc.open();
      }
      System.out.println(tc.getLookup());
      tc.requestActive();
    }
  }
}
