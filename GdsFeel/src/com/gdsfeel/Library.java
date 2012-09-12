/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import com.gdsfeel.util.Archiver;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.ini4j.Ini;

/**
 * represents GDSII DATABASE LIBRARY
 * element class: Structure
 * 
 * @author kenjiro
 */
public class Library extends GdsObject {

  private static Log log = LogFactory.getLog(Library.class);
  private static String PRIMARY_EXTENSION_BODY = "DB";
  private static String[] HANDLED_EXTENSION_BODIES = {PRIMARY_EXTENSION_BODY};
  private static String EXTRACT_AREA_NAME = ".editlibs";
  private static int DEFAULT_DBU = 1000;
  private static String DEFAULT_UNIT = "MM";
  private static String LIB_INFO_NAME = "LIB.ini";
  private static String LAYERS_NAME = "layers.xml";
  private static RE NAME_RE = new RE("^([A-Z]){1}[A-Z0-9$_]*$");

  private File dbFile;
  private File extractedFolder;
  private String name;
  private int dbu;
  private String unit;
  private Map<String, Structure> nameMap;
  private Layers layers;
  

  public Library(File dbFile) {
    Validate.notNull(dbFile);
    Validate.isTrue(isValid(dbFile));

    this.dbFile = dbFile;
    extractedFolder = null;
    nameMap = new HashMap<String, Structure>();
    layers = new Layers();
  }

  public boolean isOpen() {
    if (extractedFolder == null) {
      return false;
    }
    return extractedFolder.isDirectory();
  }

  
  public boolean isClose() {
    return ! isOpen();
  }


  public void open() throws IOException {
    if (isOpen()) {
      return;
    }
    FileUtils.forceMkdir(extractedFolder());
    if (!extractedFolder().isDirectory()) {
      extractedFolder().mkdirs();
    }
    Archiver.extract(dbFile, extractedFolder());
    loadLibInfo();
    Validate.isTrue(libInfoFile().isFile());
    if (layersFile().isFile()) {
      loadLayers();
    }
    lookupStructures();
  }


  public void close() throws IOException {
    if (!isOpen()) {
      return;
    }
    FileUtils.forceDelete(extractedFolder());
    Validate.isTrue(!libInfoFile().exists());
  }


  public Structure structureNamed(String structureName)  {
    openIfClosed();
    if (nameMap.containsKey(Structure.asKey(structureName))) {
      return nameMap.get(Structure.asKey(structureName));
    }
    return null;
  }



  public void openIfClosed()  {
    if (! isOpen()) {
      try {
        open();
      }
      catch (IOException ex) {
        log.error(ex);
      }
    }
  }


  public void closeIfOpend()  {
    if (isOpen()) {
      try {
        close();
      }
      catch (IOException ex) {
        log.error(ex);
      }
    }
  }


  private File libInfoFile() {
    return new File(extractedFolder(), LIB_INFO_NAME);
  }

  
  private File layersFile() {
    return new File(extractedFolder(), LAYERS_NAME);
  }


  private void loadLayers() {
    Validate.isTrue(layersFile().isFile());
    layers.loadFromXmlFile(layersFile());
  }

  
  public Color colorForLayerNumber(int layerNumber) {
    return layers.atNumber(layerNumber).getColor();
  }

  private void loadLibInfo() {
    Validate.isTrue(libInfoFile().exists());

    Ini ini = new Ini();
    FileReader reader = null;
    try {
      reader = new FileReader(libInfoFile());
      ini.load(reader);
      Ini.Section section = ini.get("INITLIB");
      name = section.get("name");
      dbu = Integer.parseInt(section.get("dbu"));
      unit = section.get("unit");
    }
    catch (IOException ex) {
      log.warn(ex);
    }
    finally {
      IOUtils.closeQuietly(reader);
    }
  }

  private void lookupStructures() {
    File dir = extractedFolder();
//    Collection subDirs = FileUtils.listFiles(dir, new String[] {"structure"}, false);

    for (File d : dir.listFiles()) {
      if (! d.isDirectory()) continue;
      if (! d.getName().endsWith(".structure")) continue;
      Structure newInstance = new Structure(this, d);
      nameMap.put(newInstance.getKeyName(), newInstance);
    }
  }

  private File extractedFolder() {
    if (extractedFolder == null) {
      extractedFolder = new File(Library.extractAreaFolder(),
                                 getNameWithExtension());
    }
    return extractedFolder;
  }

  public String getName() {
    if (isOpen()) {
      return name;
    }
    return Library.getName(dbFile.getName());
  }


  public String[] getStructureNames()  {
    openIfClosed();
    return new TreeSet<String>(nameMap.keySet()).toArray(new String[0]);
  }


  public String getNameWithExtension() {
    return Library.getNameWithExtension(getName());
  }

  public int getDbu() {
    return dbu;
  }

  public String getUnit() {
    return unit;
  }

  public static String[] getClosedNames() {
    return new String[0];
  }

  public static String[] getOpendNames() {
    return new String[0];
  }

  private static File[] getFiles(File pathToGdsFeel) {
    return (File[]) FileUtils.listFiles(pathToGdsFeel,
                                        FileFilterUtils.asFileFilter(new FileFilter() {

      
      public boolean accept(File pathname) {
        return isValid(pathname);
      }
    }), null).toArray(new File[0]);
  }

  public static File[] getFiles() {
    return getFiles(Config.getProjectFolder());
  }

  /**
   * stripped extension name
   * 
   * @param libName
   * @return "abc.db" -> "ABC"
   */
  public static String getName(String libName) {
    return FilenameUtils.getBaseName(libName).toUpperCase();
  }

  /**
   * @param libName
   * @return "abc" -> "ABC.DB"
   */
  public static String getNameWithExtension(String libName) {
    return getName(libName) + "." + PRIMARY_EXTENSION_BODY;
  }

  /**
   * @return array of basenames
   */
  public static String[] getNames() {
    List<String> names = new ArrayList<String>();
    for (File f : getFiles()) {
      names.add(FilenameUtils.getBaseName(f.getName()));
    }
    return names.toArray(new String[0]);
  }

  public static File extractAreaFolder() {
    return new File(Config.getProjectFolder(), EXTRACT_AREA_NAME);
  }

  /**
   * tests collect zip archived database.
   * @param dbFile test files
   * @return
   */
  private static boolean isValid(File dbFile) {
    Validate.notNull(dbFile);
    if (!dbFile.isFile()) {
      return false;
    }
    if (!isValidFileName(dbFile.getName())) {
      return false;
    }
    return Archiver.hasEntryName(dbFile, LIB_INFO_NAME);
  }

  /**
   * @param fileNamePart File>>getName() value
   * @return true if success
   */
  public static boolean isValidFileName(String fileNamePart) {
    Validate.notEmpty(fileNamePart);
    //  A.TAR.DB -> Invarid 2 extension
    if (StringUtils.countMatches(fileNamePart, ".") > 1) {
      return false;
    }
    if (!FilenameUtils.isExtension(fileNamePart, HANDLED_EXTENSION_BODIES)) {
      return false;
    }
    return NAME_RE.match(FilenameUtils.getBaseName(fileNamePart));
  }
}
