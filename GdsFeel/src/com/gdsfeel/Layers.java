/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gdsfeel;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author kenjiro
 */
public class Layers {
  private static Log log = LogFactory.getLog(Layers.class);
  private Map<Integer,Layer> _map;

  
  public Layers() {
    _map = new HashMap<Integer,Layer>();
  }


  public void loadFromXmlFile(File xmlFile) {
    DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder b = df.newDocumentBuilder();
      Document doc = b.parse(xmlFile);
      Element root = doc.getDocumentElement();
      NodeList nl = root.getElementsByTagName("layer");

      for (int i = 0; i < nl.getLength() ; i++) {
        Element e = (Element)nl.item(i);
        Layer l = new Layer();
        l.loadFromXmlElement(e);
        _map.put(l.getNumber(), l);
      }
    }
    catch (Exception ex) {
      log.warn(ex);
    }
  }


  public Layer atNumber(int gdsno) {
    if (! _map.containsKey(gdsno)) {
      Layer newLayer = new Layer(gdsno);
      _map.put(gdsno, newLayer);
    }
    return _map.get(gdsno);
  }


  public int[] getNumbers() {
    int[] result = ArrayUtils.toPrimitive(_map.keySet().toArray(new Integer[0]));
    Arrays.sort(result);
    return result;
  }
  
}
