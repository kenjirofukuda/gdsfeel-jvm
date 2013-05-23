/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gdsfeel;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class Config {

  private static Log log = LogFactory.getLog(Config.class);

  public static String pathToSmalltalkProject() {
    return lookupProjectPath();
  }

  public static File getProjectFolder() {
    return new File(pathToSmalltalkProject());
  }

  private static String lookupProjectPath() {
    PropertiesConfiguration conf;
    String result = "";
    try {
      conf = new PropertiesConfiguration(Config.getConfigFile());
      if (! conf.getFile().exists()) {
        conf.addProperty("project.path", "");
        conf.save();
      }
      result = conf.getString("project.path", "");
    }
    catch (ConfigurationException ex) {
      log.fatal(ex);
    }
    return result;
  }


  public static void setProjectFolder(File gdsFeelFolder) {
    PropertiesConfiguration conf;
    try {
      conf = new PropertiesConfiguration(Config.getConfigFile());
      conf.setProperty("project.path", gdsFeelFolder.getAbsolutePath());
      conf.save();
    }
    catch (ConfigurationException ex) {
      log.fatal(ex);
    }
  }
  

  public static boolean isSetupCompleted() {
    return getProjectFolder().isDirectory() 
        && getProjectFolder().canWrite(); 
  }

  
  public static File getConfigFile() {
    File configDir = new File(SystemUtils.getUserHome(), ".GdsFeel");
    File configFile = new File(configDir, "main.properties");
    return configFile;
  }

}
