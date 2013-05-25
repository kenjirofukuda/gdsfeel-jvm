/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author kenjiro
 */
public class Kinds {
  public static final Kind[] KEYS = new Kind[]{
    Kind.BD,
    Kind.PB,
    Kind.PC,
    Kind.SO,
    Kind.SE,
    Kind.AO,
    Kind.AE,
    Kind.AB
  };

  private static final Kind[] DEFAULT_VISIBLE_KINDS = new Kind[]{
    Kind.BD, Kind.PB
  };

  private static final Kind[] DEFAULT_SELECTABLE_KINDS = new Kind[]{
    Kind.BD, Kind.PB, Kind.PC, Kind.SO, Kind.AO
  };
  
  private static Kinds instance;

  private Map<Kind, Kind.Entry> map;

  public Kinds() {
    initialize();
    setupToDefaults();
  }

  private void initialize() {
    this.map = new HashMap<>();
    for (Kind k : KEYS) {
      map.put(k, new Kind.Entry(k));
    }
  }

  private void setupToDefaults() {
    selectableKindsAllOff();
    visibleKindsToOn(getDefaultSelectableKinds());
    visibleKindsAllOff();
    visibleKindsToOn(getDefaultVisibleKinds());
  }

  public Kind.Entry get(Kind k) {
    return map.get(k);
  }
  
  public static Kinds getInstance() {
    if (instance == null) {
      instance = new Kinds();
    }
    return instance;
  }

  private Kind[] getDefaultVisibleKinds() {
    return DEFAULT_VISIBLE_KINDS;
  }

  private Kind[] getDefaultSelectableKinds() {
    return DEFAULT_SELECTABLE_KINDS;
  }

  public void selectableKindsTo(Kind[] kinds, boolean value) {
    for (Kind k : kinds) {
      get(k).setSelectable(value);
    }
  }

  public void selectableKindsAllTo(boolean value) {
    selectableKindsTo(KEYS, value);
  }

  /**
   * *
   * GDSII comand SKINDON
   *
   * @param kinds
   */
  public void selectableKindsToOn(Kind[] kinds) {
    selectableKindsTo(kinds, true);
  }

  /**
   * *
   * GDSII comand SKINDOFF
   *
   * @param kinds
   */
  public void selectableKindsToOff(Kind[] kinds) {
    selectableKindsTo(kinds, false);
  }

  public void selectableKindsAllOn() {
    selectableKindsAllTo(true);
  }

  public final void selectableKindsAllOff() {
    selectableKindsAllTo(false);
  }

  public void visibleKindsTo(Kind[] kinds, boolean value) {
    for (Kind k : kinds) {
      get(k).setSelectable(value);
    }
  }

  public void visibleKindsAllTo(boolean value) {
    visibleKindsTo(KEYS, value);
  }

  /**
   * *
   * GDSII comand VKINDON
   *
   * @param kinds
   */
  public void visibleKindsToOn(Kind[] kinds) {
    visibleKindsTo(kinds, true);
  }

  /**
   * *
   * GDSII comand VKINDOFF
   *
   * @param kinds
   */
  public void visibleKindsToOff(Kind[] kinds) {
    visibleKindsTo(kinds, false);
  }

  public void visibleKindsAllOn() {
    visibleKindsAllTo(true);
  }

  public void visibleKindsAllOff() {
    visibleKindsAllTo(false);
  }
}
