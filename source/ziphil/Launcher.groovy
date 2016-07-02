package ziphil

import groovy.transform.CompileStatic
import ziphil.main.MainApplication


@CompileStatic @Newify
public class Launcher {

  public static final String TITLE = "ZpDIC alpha"
  public static final String VERSION = "0.4.0α"
  public static final String DATE = "1622"
  public static final Boolean DEBUG = false
  public static final String BASE_PATH = createBasePath()

  public static void main(String... args) {
    println("Java version: ${Runtime.getPackage().getImplementationVersion()}")
    println("Groovy version: ${GroovySystem.getVersion()}")
    MainApplication.launch(MainApplication, args)
  }

  private static String createBasePath() {
    String classPath = System.getProperty("java.class.path")
    Integer classIndex = classPath.indexOf(File.pathSeparator)
    String firstPath = (classIndex != -1) ? classPath.take(classIndex) : classPath
    File file = File.new(firstPath)
    String filePath = file.getCanonicalPath()
    String path
    if (file.isDirectory()) {
      path = filePath + File.separator
    } else {
      Integer fileIndex = filePath.lastIndexOf(File.separator)
      path = filePath.take(fileIndex) + File.separator
    }
    return path
  }

}



// ◆ Version History
//
//  0. 4. 0 | OneToMany 形式の表示や編集を行う機能を追加。
//  0. 3. 0 | アクセント記号の有無や大文字小文字の違いなどを無視するオプションを追加。
//          | シャレイア語辞典形式のテキスト装飾に対応。
//  0. 2. 0 | メニューからすぐに開けるように辞書を登録する機能を追加。
//          | 辞書を新規作成する機能を追加。
//          | 単語データの編集画面でのテキストエリアのフォントを設定する機能を追加。
//  0. 1. 0 | 単語データの表示欄のフォントなどを設定する機能を追加。
//          | 辞書データをオートセーブするかどうかを選択式に変更。
//          | 辞書ファイルをメニューから開く形式に変更。
//  0. 0. 0 | 初期バージョン。