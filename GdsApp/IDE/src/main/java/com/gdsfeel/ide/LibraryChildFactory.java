/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ide;

import com.gdsfeel.Library;
import com.gdsfeel.Station;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author kenjiro
 */
class LibraryChildFactory extends ChildFactory<Library> {

  public LibraryChildFactory() {
  }

  @Override
  protected Node createNodeForKey(Library key) {
    return new LibraryNode(key);
  }
  static Station station;

  static public Station getStation() {
    if (station == null) {
      station = new Station();
      station.setup();
    }
    return station;
  }

  @Override
  protected boolean createKeys(
          List<Library> list) {
    list.addAll(getStation().getLibraries());
    return true;
  }
}
