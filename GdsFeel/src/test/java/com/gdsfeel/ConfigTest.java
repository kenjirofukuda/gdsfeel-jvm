/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.io.File;
import junit.framework.TestCase;

/**
 *
 * @author kenjiro
 */
public class ConfigTest extends TestCase {

  public ConfigTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    //super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    // super.tearDown();
  }

  /**
   * Test of pathToSmalltalkProject method, of class Config.
   */
  public void testPathToSmalltalkProject() {
    System.out.println("pathToSmalltalkProject");
    String expResult = "";
    String result = Config.pathToSmalltalkProject();
    System.out.println(result);
    assertNotNull(result);
    assertNotSame(result, "");
  }

  /**
   * Test of getProjectFolder method, of class Config.
   */
  public void testGetProjectFolder() {
    System.out.println("getProjectFolder");
    File result = Config.getProjectFolder();
    System.out.println(result);
    assertNotNull(result);
    assertTrue("project folder must exists", result.exists());
    assertTrue("project folder must directory", result.isDirectory());
  }

//  /**
//   * Test of setProjectFolder method, of class Config.
//   */
//  public void testSetProjectFolder() {
//    System.out.println("setProjectFolder");
//    File gdsFeelFolder = null;
//    Config.setProjectFolder(gdsFeelFolder);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
//  }
  /**
   * Test of isSetupCompleted method, of class Config.
   */
  public void testIsSetupCompleted() {
    System.out.println("isSetupCompleted");
    boolean expResult = true;
    boolean result = Config.isSetupCompleted();
    System.out.println(result);
    assertEquals(expResult, result);
  }

  /**
   * Test of getConfigFile method, of class Config.
   */
  public void testGetConfigFile() {
    System.out.println("getConfigFile");
    File expResult = null;
    File result = Config.getConfigFile();
    System.out.println(result);
    assertNotNull(result);
    assertTrue("config file must exists", result.exists());
    assertTrue("config file must file", result.isFile());
  }
}
