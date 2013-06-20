/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.ide;

import com.gdsfeel.Library;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kenjiro
 */
public class OpenLibraryList {

  private static OpenLibraryList INSTANCE;
  private List<Library> openLibraries;

  OpenLibraryList() {
    openLibraries = new ArrayList<Library>();
  }

  public static OpenLibraryList getDefault() {
    if (INSTANCE == null) {
      INSTANCE = new OpenLibraryList();
    }
    return INSTANCE;
  }

  public static Library fileToLibrary(File libraryFile) { //
    return null;
  }
}
