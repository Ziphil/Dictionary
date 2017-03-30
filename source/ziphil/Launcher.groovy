package ziphil

import groovy.transform.CompileStatic
import ziphil.main.MainApplication
import ziphil.module.Version
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Launcher {

  public static final String TITLE = "ZpDIC fetith"
  public static final Version VERSION = Version.new(1, 7, 1, 1894)
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
//  1. 7. 1 | akrantiain で単語間のマッチが正しく行われない不具合を修正。
//  1. 7. 0 | akrantiain を実行する機能を追加。
//          | シャッフル後にソートするタイプの hah 圧縮を行う機能を追加。
//  1. 6. 0 | hah 圧縮を行う機能を追加。
//          | シャレイア語辞典形式での活用サジェストに対応。
//          | エラーが発生したときにバックアップを保存するよう変更。
//  1. 5. 2 | メインウィンドウを閉じたときに単語編集ウィンドウが閉じない不具合を修正。
//  1. 5. 1 | 別の辞書を開いたときに単語編集ウィンドウを閉じるよう修正。
//  1. 5. 0 | スクリプト検索機能を追加。
//  1. 4. 0 | 登録辞書の表示名を変更する機能を追加。
//          | 検索条件を保存してメニューから呼び出す機能を追加。
//          | GUI フォントを設定する機能を追加。
//  1. 3. 0 | 前の検索結果に戻る機能を追加。
//          | OneToMany 形式の内容の表示順を固定する機能を追加。
//          | OneToMany 形式の単語の修正時に登録ラベルが減らない不具合を修正。
//  1. 2. 0 | 内容のラベルの後で改行するかを設定する機能を追加。
//          | OneToMany 形式の単語の新規作成時のデフォルトデータを変更する機能を追加。
//          | 単語表示欄の行間を調整する機能を追加。
//  1. 1. 0 | 検索結果をシャッフルする機能を追加。
//          | OneToMany 形式で全文検索しても何も表示されない不具合を修正。
//  1. 0. 0 | 多くの機能の細かな挙動を改善。
//          | 多くの細かな不具合を修正。
//  0. 8. 1 | OneToMany 形式の読み込みや編集を高速化。
//  0. 8. 0 | ファイルをドラッグアンドドロップで開く機能を追加。
//          | 現在開いている辞書を登録辞書に登録する機能を追加。
//  0. 7. 2 | 単語リストの空白部分をダブルクリックするとエラーになる不具合を修正。
//  0. 7. 1 | 単語の表示順序が正しくない不具合を修正。
//  0. 7. 0 | OneToMany 形式の項目の順番を入れ替える機能を追加。
//          | OneToMany 形式を表示する際のアルファベット順を設定する機能を追加。
//  0. 6. 1 | ファイルを開くときにキャンセルするとエラーダイアログが表示される不具合を修正。
//  0. 6. 0 | OneToMany 形式で変化形サジェストを行う機能を追加。
//          | OneToMany 形式で関連語をクリックするとその単語を表示するよう変更。
//          | ヘルプに基本操作に関する説明を追加。
//  0. 5. 0 | OneToMany 形式の編集画面で使えるショートカットキーを追加。
//          | 高度な検索機能を追加。
//          | ヘルプを確認できる画面を追加。
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