/*
 * Archiver.java
 *
 * Created on 2006/07/26, 10:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.gdsfeel.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author kenjiro
 */
public class Archiver {

  private static Log log = LogFactory.getLog(Archiver.class);

  /**
   * Creates a new instance of Archiver
   */
  private Archiver() {
  }

  public static void extract(File zipFile, File intoFolder)
          throws ZipException, IOException {
    try (ZipFile zf = new ZipFile(zipFile, ZipFile.OPEN_READ)) {
      Enumeration<? extends ZipEntry> en = zf.entries();
      while (en.hasMoreElements()) {
        ZipEntry ze = en.nextElement();
        if (ze.isDirectory()) {
          new File(intoFolder, ze.getName()).mkdirs();
        } 
        else {
          File parent =
                  new File(intoFolder, ze.getName()).getParentFile();
          if (parent != null) {
            parent.mkdirs();
          }
          InputStream in;
          try (FileOutputStream out = new FileOutputStream(new File(intoFolder, ze.getName()))) {
            in = zf.getInputStream(ze);
            byte[] buf = new byte[2048];
            int size = 0;
            while ((size = in.read(buf)) != -1) {
              out.write(buf, 0, size);
            }
          }
          in.close();
        }
      }
    }
  }

  public static boolean hasEntryName(File zipFile, String entryName) {
    Set<String> names = new HashSet<>();
    try {
      entryNames(zipFile, names);
    }
    catch (ZipException ex) {
      return false;
    }
    catch (IOException ex) {
      return false;
    }
    return names.contains(entryName);
  }

  public static void printZipFile(File zipFile)
          throws ZipException, IOException {
    ArrayList<String> names = new ArrayList<>();
    entryNames(zipFile, names);
    for (String s : names) {
      System.out.println(s);
    }
  }

  public static void entryNames(File zipFile, Collection<String> names)
          throws ZipException, IOException {
    try (ZipFile zf = new ZipFile(zipFile, ZipFile.OPEN_READ)) {
      Enumeration<? extends ZipEntry> en = zf.entries();
      while (en.hasMoreElements()) {
        ZipEntry ze = en.nextElement();
        names.add(ze.getName());
      }
    }
  }

  /**
   * **
   * フォルダをZipアーカイブする
   *
   * @param folder 圧縮するフォルダー
   * @return アーカイブしたファイルのオブジェクト
   */
  public static File folderArchive(File folder) {
    File absoluteFolder = folder.getAbsoluteFile();
    if (!absoluteFolder.exists()) {
      log.warn("存在しません: " + absoluteFolder.getPath());
      return null;
    }
    if (!absoluteFolder.isDirectory()) {
      log.warn("フォルダではありません: " + absoluteFolder.getPath());
      return null;
    }

    File toFolder = folder.getParentFile();
    if (toFolder == null) {
      log.warn("適切なフォルダではありません: " + absoluteFolder.getPath());
      return null;
    }

    String zipName = absoluteFolder.getName();

    Collection<File> fileNames = FileUtils.listFiles(absoluteFolder, null, true);
    Map<File, String> entryMap = new HashMap<>();
    for (File f : fileNames) {
      String filePath = FilenameUtils.separatorsToUnix(f.getAbsolutePath());
      String archivePath = StringUtils.right(filePath,
              filePath.length()
              - FilenameUtils.getFullPath(absoluteFolder.getPath()).length());
      entryMap.put(f, archivePath);
    }
    return zipOut(entryMap, toFolder, zipName);

  }

  /**
   * ファイル群を Zip アーカイブする。
   *
   * @param entryMap 対象となるファイル群
   * @param destOut 出力先のフォルダ nullの場合、ユーザーのデスクトップフォルダ
   * @param archiveName 拡張子を除くアーカイブ名
   *
   */
  public static File zipOut(Map<File, String> entryMap,
          File destOut, String archiveName) {
    File folder;
    if (destOut == null) {
      folder = Platform.getInstance().getDesktopFolder();
    } 
    else {
      folder = destOut;
    }
    String fileName = archiveName;
    File zipFile = new File(folder, fileName + ".zip");
    if (zipFile.exists()) {
      if (!zipFile.delete()) {
        return zipFile;
      }
    }

    try {
      try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
        for (File f : entryMap.keySet()) {
          addTargetFile(zos, f, entryMap.get(f));
        }
      }
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (ZipException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return zipFile;
  }

  public static void addTargetFile(
          ZipOutputStream zos, File file, String zipEntryName)
          throws FileNotFoundException,
          ZipException, IOException {
    try {
      try (BufferedInputStream bis = new BufferedInputStream(
              new FileInputStream(file))) {
        ZipEntry target = new ZipEntry(zipEntryName);
        target.setTime(file.lastModified());
        zos.putNextEntry(target);  // target の書き込み開始

        /* バッファリングは自動的に行われるので、ここで指定している
         バッファサイズは、特別に意味は持たない  */

        byte buf[] = new byte[2048];
        int count;
        while ((count = bis.read(buf, 0, 2048)) != -1) {
          zos.write(buf, 0, count);
        }
      }
      zos.closeEntry();  // target の書き込み終了
    }
    catch (FileNotFoundException e) {
      throw e;
    }
    catch (ZipException e) {
      throw e;
    }
    catch (IOException e) {
      throw e;
    }
  }
}
