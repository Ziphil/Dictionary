package ziphil

import groovy.transform.CompileStatic
import ziphil.main.MainApplication


@CompileStatic
public class Launcher {

  public static final String TITLE = "ZpDIC alpha"
  public static final String VERSION = "0.3.0α"
  public static final String DATE = "1615"
  public static final Boolean DEBUG = false

  public static void main(String... args) {
    println("Java version: ${Runtime.getPackage().getImplementationVersion()}")
    println("Groovy version: ${GroovySystem.getVersion()}")
    MainApplication.launch(MainApplication, args)
  }

}



// ◆ Version History
//
//  0. 3. 0 | アクセント記号の有無や大文字小文字の違いなどを無視するオプションを追加。
//          | シャレイア語辞典形式のテキスト装飾に対応。
//  0. 2. 0 | メニューからすぐに開けるように辞書を登録する機能を追加。
//          | 辞書を新規作成する機能を追加。
//          | 単語データの編集画面でのテキストエリアのフォントを設定する機能を追加。
//  0. 1. 0 | 単語データの表示欄のフォントなどを設定する機能を追加。
//          | 辞書データをオートセーブするかどうかを選択式に変更。
//          | 辞書ファイルをメニューから開く形式に変更。
//  0. 0. 0 | 初期バージョン。