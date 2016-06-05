package ziphil

import groovy.transform.CompileStatic
import ziphil.main.MainApplication


@CompileStatic
public class Launcher {

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