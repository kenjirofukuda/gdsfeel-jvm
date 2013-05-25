/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kenjiro
 */
public class Station extends GdsObject {

  private List<Library> _libraries;
  private Library _library;
  private Structure _structure;
  
  public Station() {
    _libraries = new ArrayList<>();
  }

  public void setup() {
    File[] dbFiles = Library.getFiles();
    for (File f : dbFiles) {
      _libraries.add(new Library(f));
    }
    setupDefaultVisibility();
  }

  public void tearDown() {
    for (Library lib : _libraries) {
      lib.tryClose();
    }
    _libraries = null;
  }

  public List<Library> getLibraries() {
    return _libraries;
  }

  public Library getLibrary() {
    return _library;
  }

  public Structure getStructure() {
    return _structure;
  }

  public Library activateLibraryNamed(String libname) {
    _library = libraryNamed(libname);
    return _library;
  }

  public void activateStructureNamed(String strucname) {
    if (_library == null) {
      return;
    }
    _structure = _library.structureNamed(strucname);
  }

  private Library libraryNamed(String libname) {
    for (Library l : _libraries) {
      if (l.getName().equalsIgnoreCase(libname)) {
        return l;
      }
    }
    return null;
  }
  
  private static final Kind[] DEFAULT_VISIBLE_KINDS = new Kind[] {
    Kind.BD, Kind.PB
  };
  
  private Kind[] getDefaultVisibleKinds() {
    return DEFAULT_VISIBLE_KINDS;
  }
  
  private void setupDefaultVisibility() {
    for (Kind k : getDefaultVisibleKinds()) { 
    }
  }  
}
