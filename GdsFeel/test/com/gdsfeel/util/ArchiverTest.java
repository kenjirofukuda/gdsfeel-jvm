/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel.util;

import com.gdsfeel.Library;
import java.io.File;
import java.util.Map;
import java.util.zip.ZipOutputStream;
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
public class ArchiverTest {

  public ArchiverTest() {
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
   * Test of printZipFile method, of class Archiver.
   */
  @Test
  public void testPrintZipFile() throws Exception {
    System.out.println("printZipFile");
    File zipFile = Library.getFiles()[0];
    Archiver.printZipFile(zipFile);
  }

  /**
   * Test of extract method, of class Archiver.
   */
  @Test
  public void testExtract() throws Exception {
    System.out.println("extract");
    File zipFile = Library.getFiles()[0];
    File intoFolder = new File(Platform.getInstance().getDesktopFolder(), zipFile.getName());
    intoFolder.mkdirs();
    Archiver.extract(zipFile, intoFolder);
  }



  

  
}