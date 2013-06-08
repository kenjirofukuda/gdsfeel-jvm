/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kenjiro
 */
public class StructureTest extends LibraryTest {

  public StructureTest(String testName) {
    super(testName);
  }
  private Structure structure;

  private void structureLoop() {
    for (String n : getLibrary().getStructureNames()) {
      Structure s = getLibrary().structureNamed(n);
      s.getElements();
    }
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testGetLibrary() {
  }

  public void testGetName() {
  }

  public void testGetKeyName() {
  }

  public void testAsKey() {
  }

  public void testGetElements() {
    puts("testGetElements");
    exampleCountElementsReport();
    puts("");
  }

  private void exampleCountElementsReport() {
    Map<String, Integer> map = new HashMap<>();
    for (String n : getLibrary().getStructureNames()) {
      Structure s = getLibrary().structureNamed(n);
      map.put(s.getName(), s.getElementArray().length);
    }
    ArrayList<Map.Entry<String, Integer>> l = new ArrayList<>();
    l.addAll(map.entrySet());
    Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Map.Entry<String, Integer> v1,
                         Map.Entry<String, Integer> v2) {
        return v1.getValue() > v2.getValue() ? -1 : 1;
      }
    });

    for (Map.Entry<String, Integer> ent : l) {
      System.out.printf("%-10s: %6d\n", ent.getKey(), ent.getValue());
    }
  }

  public void testGetBoundingBox() {
    puts("testGetBoundingBox");
    exampleBoundingSizeReport();
    puts("");
  }

  private void exampleBoundingSizeReport() {
    Map<String, Rectangle2D> map = new HashMap<>();
    for (String n : getLibrary().getStructureNames()) {
      Structure s = getLibrary().structureNamed(n);
      map.put(s.getName(), s.getBoundingBox());
    }

    ArrayList<Map.Entry<String, Rectangle2D>> l = new ArrayList<>();
    l.addAll(map.entrySet());
    Collections.sort(l, new Comparator<Map.Entry<String, Rectangle2D>>() {
      @Override
      public int compare(Map.Entry<String, Rectangle2D> v1,
                         Map.Entry<String, Rectangle2D> v2) {
        return v1.getValue().getWidth() > v2.getValue().getWidth() ? -1 : 1;
      }
    });

    for (Map.Entry<String, Rectangle2D> ent : l) {
      System.out.printf("%-10s: [% 10.3f, % 10.3f]\n",
                        ent.getKey(), ent.getValue().getWidth(), ent.getValue().getHeight());
    }
  }

  @Override
  public void testColorForLayerNumber() {
  }

  public void testLoad() {
  }
}
