package ziphil

import groovy.transform.CompileStatic
import ziphil.main.MainApplication


@CompileStatic
public class Launcher {

  public static final String TITLE = "ZpDIC alpha"
  public static final String VERSION = "0.0.0α"
  public static final String DATE = "1601"
  public static final Boolean DEBUG = false

  public static void main(String... args) {
    println("Java version: ${Runtime.getPackage().getImplementationVersion()}")
    println("Groovy version: ${GroovySystem.getVersion()}")
    MainApplication.launch(MainApplication, args)
  }

}



// ◆ Version History
//
//  0. 0. 0 | 初期バージョン。