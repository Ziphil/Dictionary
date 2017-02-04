package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CustomFiles {

  public static void deleteAll(File file) {
    if (file.exists()) {
      if (file.isFile()) {
        file.delete()
      } else if (file.isDirectory()) {
        for (File childFile : file.listFiles()) {
          CustomFiles.deleteAll(childFile)
        }
        file.delete()
      }
    }
  }

}