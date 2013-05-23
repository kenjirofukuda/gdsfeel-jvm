/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.util.List;

/**
 *
 * @author kenjiro
 */
public class StationTest extends GdsTCase {

  private Station station;
  
  protected Station getStation() { return station; }
  
  
  public StationTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    station = new Station();
    station.setup();
  }
  
  @Override
  protected void tearDown() throws Exception {
    if (station != null) {
      station.tearDown();
      station = null;
    }
    super.tearDown();
  }

  public void testGetLibraries() {
    puts("testGetLibraries");
    List<Library> result = station.getLibraries();
    puts(result);
    
  }
  
  public void testActivateLibrary() {
    puts("testActivateLibrary");
    station.activateLibraryNamed("BGHIER");
    assertTrue(station.getLibrary().getName().equalsIgnoreCase("BGHIER"));
    station.getStructure();
  }

}
