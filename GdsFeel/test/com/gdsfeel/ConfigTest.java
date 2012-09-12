/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kenjiro
 */
public class ConfigTest {

  public ConfigTest() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of pathToSmalltalkProject method, of class Config.
   */
  @Test
  public void testPathToSmalltalkProject() {
    System.out.println("pathToSmalltalkProject");
    boolean expResult = true;
    System.out.println("[" + Config.pathToSmalltalkProject() + "]");
    boolean result = new File(Config.pathToSmalltalkProject()).exists();
    assertEquals(expResult, result);
  }


  /**
   * Test of getProjectFolder method, of class Config.
   */
  @Test
  public void testGetProjectFolder() {
    System.out.println("getProjectFolder");
    boolean expResult = true;
    boolean result = Config.getProjectFolder().exists();
    assertEquals(expResult, result);

    expResult = true;
    result = Config.getProjectFolder().isDirectory();
    assertEquals(expResult, result);

  }

}