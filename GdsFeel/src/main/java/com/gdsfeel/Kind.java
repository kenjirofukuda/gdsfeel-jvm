/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * BD	BOUNDARY PC	PATH CENTER PB	PATH BOUNDARY TX	TEXT TO	TEXT ORIGIN BX	BOX ND
 * NESTED DATA NO	NODE SO	STRUCTURE ORIGIN SE	STRUCTURE EXTENT AO	ARRAY ORIGIN
 * AE	ARRAY EXTENT AB	ARRAY BORDER EX	EXTERIOR DATA (STICKS)
 *
 *
 * @author kenjiro
 */
public class Kind {

  private String description;

  public String getDescription() {
    return description;
  }

  public String getAddrev() {
    return addrev;
  }
  private String addrev;
  public static final Kind BD = new Kind("BD", "boundary");
  public static final Kind PB = new Kind("PB", "path boundary");
  public static final Kind PC = new Kind("PC", "path center");
//  public static final Kind TX = new Kind("TX", "text");
//  public static final Kind TO = new Kind("TO", "text origin");
//  public static final Kind BX = new Kind("BX", "box");
//  public static final Kind ND = new Kind("ND", "nested data");
//  public static final Kind NO = new Kind("NO", "node");
  public static final Kind SO = new Kind("SO", "strucutre origin");
  public static final Kind SE = new Kind("SE", "strucutre extent");
  public static final Kind AO = new Kind("AO", "array origin");
  public static final Kind AE = new Kind("AE", "array extent");
  public static final Kind AB = new Kind("AB", "array border");
//  public static final Kind EX = new Kind("EX", "exterior data (sticks)");

  public Kind(String addrev, String description) {
    this.addrev = addrev;
    this.description = description;
  }

  public static class Entry {

    private Kind kind;

    public Kind getKind() {
      return kind;
    }
    private boolean selectable;
    private boolean visible;
    private Color color;
    public static final String PROP_SELECTABLE = "selectable";
    public static final String PROP_VISIBLE = "visible";
    public static final String PROP_COLOR = "color";

    public Entry(Kind kind) {
      this.kind = kind;
      this.selectable = false;
      this.visible = false;
      this.color = Color.WHITE;
    }

    /**
     * Get the value of color
     *
     * @return the value of color
     */
    public Color getColor() {
      return color;
    }

    /**
     * Set the value of color
     *
     * @param color new value of color
     */
    public void setColor(Color color) {
      Color oldColor = this.color;
      this.color = color;
      propertyChangeSupport.firePropertyChange(PROP_COLOR, oldColor, color);
    }

    /**
     * Get the value of selectable
     *
     * @return the value of selectable
     */
    public boolean isSelectable() {
      return selectable;
    }

    /**
     * Get the value of visible
     *
     * @return the value of visible
     */
    public boolean isVisible() {
      return visible;
    }

    /**
     * Set the value of visible
     *
     * @param visible new value of visible
     */
    public void setVisible(boolean visible) {
      boolean oldVisible = this.visible;
      this.visible = visible;
      propertyChangeSupport.firePropertyChange(PROP_VISIBLE, oldVisible, visible);
    }

    /**
     * Set the value of selectable
     *
     * @param selectable new value of selectable
     */
    public void setSelectable(boolean selectable) {
      boolean oldSelectable = this.selectable;
      this.selectable = selectable;
      propertyChangeSupport.firePropertyChange(PROP_SELECTABLE, oldSelectable, selectable);
    }
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.removePropertyChangeListener(listener);
    }
  }
}
