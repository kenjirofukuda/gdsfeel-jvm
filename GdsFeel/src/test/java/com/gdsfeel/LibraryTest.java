/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.awt.Color;

/**
 *
 * @author kenjiro
 */
public class LibraryTest extends StationTest {

  private Library library;
  
  protected Library getLibrary() {
    return library;
  }
  
  public LibraryTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    library = getStation().activateLibraryNamed("BGHIER");
    if (library == null) {
      throw new Exception("Library BGHIER not found");
    }
  }
  
  @Override
  protected void tearDown() throws Exception {
    if (library != null) {
      library.close();
      library = null;
    }
    super.tearDown();
  }

  public void testColorForLayerNumber() {    
    for (int i : library.getLayers().getNumbers()) {
      Color c = library.colorForLayerNumber(i);
      puts("LAYER: " + i + ", Color: " + c);
    }
  }

  public void testGetStructureNames() {
    puts("testGetStructureNames");
    puts(library.getStructureNames());
  }

  public void testGetNames() {
    puts("testGetNames");
    puts(Library.getNames());
  }

}
