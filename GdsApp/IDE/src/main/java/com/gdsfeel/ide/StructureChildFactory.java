/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ide;

import com.gdsfeel.Library;
import com.gdsfeel.Structure;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author kenjiro
 */
class StructureChildFactory extends ChildFactory<Structure> {

  private final Library library;

  public StructureChildFactory(Library library) {
    assert library != null;
    this.library = library;
  }

  @Override
  protected Node createNodeForKey(Structure key) {
    return new StructureNode(key);
  }

  @Override
  protected boolean createKeys(
          List<Structure> list) {
    library.getStructureNames();
    list.addAll(library.getChildren());
    return true;
  }
}
