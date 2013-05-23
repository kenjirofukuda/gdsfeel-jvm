/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import junit.framework.TestCase;

/**
 *
 * @author kenjiro
 */
public class GdsTCase extends TestCase {

  public GdsTCase(String caseName) {
    super(caseName);
  }
  
  protected void puts(Object arg) {
    System.out.println(arg);    
  }

  protected void puts(String[] args) {
    for (String s : args) {
      System.out.println(s);
    }
  }
}
