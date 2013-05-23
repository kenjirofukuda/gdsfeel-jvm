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
public class GdsObject {

  private GdsObject _parent;
  private List<GdsObject> _children;

  public GdsObject() {
    super();
    _children = new ArrayList<>();
    _parent = null;
  }


  public GdsObject getParent() {
    return _parent;
  }


  public void setParent(GdsObject parent) {
    if (parent == null) {
      if (_parent != null) {
        _parent.removeChild(this);
        _parent = null;
      }
    }
    else {
      _parent = parent;
      parent.addChild(this);
    }
  }


  private void addChild(GdsObject child) {
    Validate.notNull(child);
    _children.add(child);
  }


  private void removeChild(GdsObject child) {
    Validate.notNull(child);
    _children.remove(child);
  }


  protected void removeAllChild() {
    for (GdsObject o : _children) {
      o.setParent(null);
    }
  }


  public GdsObject[] getChildren() {
    return _children.toArray(new GdsObject[0]);
  }
}
