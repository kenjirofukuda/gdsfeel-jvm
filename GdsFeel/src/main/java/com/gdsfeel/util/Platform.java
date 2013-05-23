/*
 * Platform.java
 *
 * Created on 2006/07/25, 14:44
 *
 * $Id: Platform.java,v 1.1 2009/06/19 12:06:11 kenjiro Exp $
 */
package com.gdsfeel.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public abstract class Platform {

  private static Log log = LogFactory.getLog(Platform.class);

  /** Creates a new instance of Platform */
  private Platform() {
  }
  static Platform instance;

  public static Platform getInstance() {
    if (instance == null) {
      if (SystemUtils.IS_OS_MAC_OSX) {
        instance = new Mac();
      }
      else if (SystemUtils.IS_OS_WINDOWS) {
        instance = new Windows();
      }
      else {
        instance = new Unix();
      }
    }
    return instance;
  }

  /**
   * 現在実行中のデスクトップフォルダを取得
   */
  public abstract File getDesktopFolder();

  /**
   * コンピューター名の取得
   */
  public abstract String getComputerName();

  /**
   * 実行中のプロセス名群を取得
   */
  public abstract Set<String> getProcessNames();

  private static class Unix extends Platform {

    @Override
    public File getDesktopFolder() {
      File outFolder = new File(System.getProperty("user.home"));
      outFolder = new File(outFolder, "Desktop");
      return outFolder;
    }

    @Override
    public String getComputerName() {
      return System.getenv("HOSTNAME");
    }

    @Override
    public Set<String> getProcessNames() {
      Set<String> names = new HashSet<>();
      return names;
    }
  }

  private static class Mac extends Unix {

    @Override
    public String getComputerName() {
      //TODO: ハードコーディングは問題だが開発者の中で所有者が福田だけなので問題無し
      return "kenjiro-osx.local";
    }
  }

  private static class Windows extends Platform {

    private static Log log = LogFactory.getLog(Windows.class);

    @Override
    public File getDesktopFolder() {
      File outFolder = new File(System.getProperty("user.home"));
      outFolder = new File(outFolder, "デスクトップ");
      return outFolder;
    }

    @Override
    public String getComputerName() {
      return System.getenv("COMPUTERNAME");
    }

    @Override
    public Set<String> getProcessNames() {
      if (SystemUtils.IS_OS_WINDOWS_XP) {
        return getProcessNamesByTaskList();
      }
      return getProcessNamesByCScript();
    }

    /**
     * Windows XP 以降で使える tasklist コマンドによるプロセス名の取得
     *
     * @return .exe等、拡張子を含むイメージ名のセット
     */
    private Set<String> getProcessNamesByTaskList() {
      Set<String> names = new HashSet<>();
      String cmd = "tasklist";
      ProcessBuilder pb = new ProcessBuilder(cmd);
      try {
        Process p = pb.start();
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
          if (line.startsWith("イメージ名")) {
            continue;
          }
          if (line.startsWith("====")) {
            continue;
          }
          line = StringUtils.left(line, 27);
          if (line.trim().length() > 0) {
            names.add(line.trim());
          }
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
      return names;
    }

    /**
     * プロセス名の取得
     *
     * @return .exe等、拡張子を含むイメージ名のセット
     */
    private Set<String> getProcessNamesByCScript() {
      Set<String> names = new HashSet<>();
      String[] paths = StringUtils.split(SystemUtils.JAVA_CLASS_PATH, ";");
      //TODO: 最後に来るという仮定は別のjarに分割したときに破綻する
      String path = paths[paths.length - 1];
      path = FilenameUtils.normalize(path);
      File folder = new File(path).getParentFile();
      ArrayList<File> founds = new ArrayList<>();
      for (int i = 0; i < 2 && folder != null; i++) {
//                log.info(folder.getPath());
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"vbs", "bat", "exe"}, true);
        for (File f : files) {
          String path2 = FilenameUtils.separatorsToUnix(f.getAbsolutePath());
          if (path2.indexOf("/helper/win/") >= 0) {
            founds.add(f);
          }
        }
        if (founds.size() > 0) {
          break;
        }
        folder = folder.getParentFile();
      }

      log.debug(founds);
      if (founds.isEmpty()) {
        log.warn("スクリプトファイルを取得できませんでした");
        return names;
      }

      File scriptFile = FileUtils.convertFileCollectionToFileArray(founds)[0];

      ProcessBuilder pb = new ProcessBuilder("CScript", "//Nologo",
        "\"" + scriptFile.getPath() + "\"");
//            pb.redirectErrorStream(true);
      try {
        Process p = pb.start();
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
          if (line.trim().length() > 0) {
            names.add(line.trim());
          }
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
      return names;
    }
  }
}


