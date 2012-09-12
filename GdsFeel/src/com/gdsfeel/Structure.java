/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdsfeel;

import com.gdsfeel.elements.GdsElement;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * GDSII STRUCTURE
 *
 * container class: Library
 *
 * @author kenjiro
 */
public class Structure extends GdsObject {

  private static Log log = LogFactory.getLog(Structure.class);

  private static String XML_EXT = "gdsfeelbeta";

  private File directory;
  private boolean loaded;
  private Rectangle2D boundingBox;


  public Structure(Library library, File directory) {
    super();
    Validate.notNull(library);
    Validate.notNull(directory);
    Validate.notNull(directory.isDirectory());
    setParent(library);
    this.directory = directory;
//    elements = new ArrayList<GdsElement>();
    loaded = false;
  }


  public Library getLibrary() {
    return (Library) getParent();
  }


  public String getName() {
    return FilenameUtils.getBaseName(directory.getName()).toUpperCase();
  }


  public String getKeyName() {
    return asKey(getName());
  }

  
  public static String asKey(String structureName) {
    return structureName.toUpperCase();
  }


  private void addElement(GdsElement element) {
    element.setParent(this);
  }


  private void removeElement(GdsElement element) {
    element.setParent(null);
  }

  
  public GdsElement[] getElements() {
    load();
    List<GdsObject> list = Arrays.asList(getChildren());
    return (GdsElement[]) list.toArray(new GdsElement[0]);
  }


  public Rectangle2D getBoundingBox() {
    if (boundingBox == null) {
      boundingBox = lookupBoundingBox();
    }
    return boundingBox;
  }

  
  private Rectangle2D lookupBoundingBox() {
    double xmin = GdsElement.BIG_VAL;
    double xmax = -GdsElement.BIG_VAL;
    double ymin = GdsElement.BIG_VAL;
    double ymax = -GdsElement.BIG_VAL;
    for (GdsElement e : getElements()) {
      Rectangle2D r = e.getBoundingBox();
      log.debug(r);
      for (Point2D p : GdsElement.calcClosedOutlinePoints(r)) {
        if (p.getX() < xmin) xmin = p.getX();
        if (p.getX() > xmax) xmax = p.getX();
        if (p.getY() < ymin) ymin = p.getY();
        if (p.getY() > ymax) ymax = p.getY();
      }
    }
    Rectangle2D result =  new Rectangle2D.Double();
    result.setFrameFromDiagonal(xmin, ymin, xmax, ymax);
    return result;
  }

  
  public Color colorForLayerNumber(int layerNumber) {
    return getLibrary().colorForLayerNumber(layerNumber);
  }

  public void load() {
    if (! loaded) {
      forceLoad();
      loaded = true;
    }
  }


  private void forceLoad() {
    removeAllChild();
    DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder b = df.newDocumentBuilder();
      Document doc = b.parse(currentFile());
      Element root = doc.getDocumentElement();
      NodeList nl = root.getElementsByTagName("element");

      for (int i = 0; i < nl.getLength() ; i++) {
        Element e = (Element)nl.item(i);
        GdsElement el = GdsElement.fromXml(e);
        if (el == null) continue;
        addElement(el);
      }
    }
    catch (Exception ex) {
      log.warn(ex);
    }
  }


  private File currentFile() {
    File f =  generationFileAt(Collections.max(generationNumbers()));
    Validate.isTrue(f.isFile());
    return f;
  }


  private File generationFileAt(int number) {
    File f = new File(directory,
        StringUtils.join(new String[] {
        getName(), Integer.toString(number), XML_EXT}, "."));
    return f;
  }


  private List<Integer> generationNumbers() {
    List<Integer> numbers = new ArrayList<Integer>();
    Collection files = FileUtils.listFiles(directory,
                                           new String[] {XML_EXT},
                                           false);
    Validate.notEmpty(files);
    for (Object o : files) {
      File f = (File) o;
      numbers.add(generationNumberOf(f));
    }
    return numbers;
  }


  private int generationNumberOf(File xmlFile) {
    String[] items = StringUtils.split(xmlFile.getName(), ".");
    if (items.length != 3) return 0;
    return Integer.parseInt(items[1]);
  }

}
