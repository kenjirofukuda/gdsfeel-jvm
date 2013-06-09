/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;

/**
 *
 * @author kenjiro
 */
public class GdsObject<P extends GdsObject, E extends GdsObject> {

  private P parent;
  private List<E> children;
  private E[] dummy;

  public GdsObject() {
    super();
    children = new ArrayList<>();
    parent = null;
  }

  public P getParent() {
    return parent;
  }

  public void setParent(P parent) {
    if (parent == null) {
      if (this.parent != null) {
        this.parent.removeChild(this);
        this.parent = null;
      }
    }
    else {
      this.parent = parent;
      parent.addChild(this);
    }
  }

  void addChild(E child) {
    Validate.notNull(child);
    children.add(child);
  }

  void removeChild(E child) {
    Validate.notNull(child);
    children.remove(child);
  }

  protected void removeAllChild() {
    for (GdsObject o : children) {
      o.setParent(null);
    }
  }

  public List<E> getChildren() {
    return children;
  }
}
