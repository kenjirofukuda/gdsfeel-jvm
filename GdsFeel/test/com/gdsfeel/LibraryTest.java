/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import java.io.File;
import org.apache.commons.lang.ArrayUtils;
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
public class LibraryTest {

  public LibraryTest() {
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
   * Test of getFiles method, of class Library.
   */
  @Test
  public void testGetFiles_0args() {
    System.out.println("getFiles");
    File[] expResult = null;
    File[] result = Library.getFiles();
    //assertEquals(expResult, result);
    System.out.println(ArrayUtils.toString(result));
  }


  /**
   * Test of getNames method, of class Library.
   */
  @Test
  public void testGetNames() {
    System.out.println("getNames");
    String[] expResult = null;
    String[] result = Library.getNames();
    System.out.println(ArrayUtils.toString(result));
  }

  
  /**
   * Test of open method, of class Library.
   */
  @Test
  public void testOpen() throws Exception {
    System.out.println("open");
    for (File dbFile : Library.getFiles()) {
      privateTestOpen(dbFile);
    }
  }

  
  private void privateTestOpen(File dbFile) throws Exception {
    Library instance = new Library(dbFile);
    instance.open();
    assertTrue(instance.isOpen());
    instance.close();
    assertFalse(instance.isOpen());
  }


  /**
   * Test of isValidFileName method, of class Library.
   */
  @Test
  public void testIsValidFileName() {
    System.out.println("isValidFileName");
    String fileNamePart = "";
    boolean expResult = false;
    boolean result = false;

    fileNamePart = "ABC.DB";
    expResult = true;
    result = Library.isValidFileName(fileNamePart);
    assertEquals(expResult, result);

    fileNamePart = "ABC$DEF.DB";
    expResult = true;
    result = Library.isValidFileName(fileNamePart);
    assertEquals(expResult, result);

    fileNamePart = "abc$def.DB";
    expResult = false;
    result = Library.isValidFileName(fileNamePart);
    assertEquals(expResult, result);

    fileNamePart = "_ABC.DB";
    expResult = false;
    result = Library.isValidFileName(fileNamePart);
    assertEquals(expResult, result);

  }
 
}