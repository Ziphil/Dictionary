package ziphil

import groovy.transform.CompileStatic
import ziphil.main.MainApplication
import ziphil.module.JavaVersion
import ziphil.module.Version
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Launcher {

  public static final String TITLE = "ZpDIC fetith"
  public static final Version VERSION = Version.new(1, 28, 3, 2253)
  public static final String FILE_SEPARATOR = createFileSeparator()
  public static final String PATH_SEPARATOR = createPathSeparator()
  public static final String LINE_SEPARATPR = createLineSeparator()
  public static final String BASE_PATH = createBasePath()

  public static void main(String... args) {
    println("Java version: ${JavaVersion.current()}")
    println("Groovy version: ${GroovySystem.getVersion()}")
    MainApplication.launch(MainApplication, args)
  }

  private static String createBasePath() {
    String classPath = System.getProperty("java.class.path")
    Int classIndex = classPath.indexOf(Launcher.PATH_SEPARATOR)
    String firstPath = (classIndex >= 0) ? classPath.take(classIndex) : classPath
    File file = File.new(firstPath)
    String filePath = file.getCanonicalPath()
    String path
    if (file.isDirectory()) {
      path = filePath + Launcher.FILE_SEPARATOR
    } else {
      Int fileIndex = filePath.lastIndexOf(Launcher.FILE_SEPARATOR)
      path = filePath.take(fileIndex) + Launcher.FILE_SEPARATOR
    }
    return path
  }

  private static String createFileSeparator() {
    return System.getProperty("file.separator").charAt(0).toString()
  }

  private static String createPathSeparator() {
    return System.getProperty("path.separator").charAt(0).toString()
  }

  private static String createLineSeparator() {
    return System.getProperty("line.separator")
  }

}



// ◆ Version History
//
//  1.28. 3 | 辞書の新規作成後に単語リストに何も表示されなくなる不具合を修正。
//  1.28. 2 | 辞書の新規作成をしようとするとエラーになる不具合を修正。
//  1.28. 1 | 環境設定をした後にエラーになる場合がある不具合を修正。
//  1.28. 0 | 辞典データを PDF 形式にエクスポートする機能を追加。
//          | 単語リストで訳語と内容を区切る線の表示の有無を設定できるよう変更。
//          | Java 9 での動作に対応。
//  1.27. 1 | OneToMany 形式の単語データの表示が特定の場合に崩れる不具合を修正。
//  1.27. 0 | 表示している辞書を再読み込みする機能を追加。
//          | OneToMany 形式で変化形を単語リストに表示するかどうかを設定する機能を追加。
//  1.26. 1 | 単語自動生成時に不正なファイルがあるとエラーになる不具合を修正。
//  1.26. 0 | 文字頻度解析の結果を CSV や TSV に出力する機能を追加。
//          | OneToMany 形式の ID を連番に振り直す機能を追加。
//  1.25. 0 | PDIC-DIC 形式を読み込む機能を追加。
//          | OneToMany 形式の単語データをコピーペーストすると不正な関連語データができ得た不具合を修正。
//          | OneToMany 形式で関連語の相互参照をしようとするとエラーになる不具合を修正。
//  1.24. 0 | akrantiain 実行ダイアログに辞典に埋め込まれたデータを使うオプションを追加。
//          | メインウィンドウの単語リストで訳語と内容の間に罫線を表示するよう変更。
//          | 文一括検索の検索結果の表示方法を変更。
//  1.23. 1 | OneToMany 形式の単語をペーストすると ID が重複する可能性があった不具合を修正。
//          | 名前をつけて保存したときにタブのファイル名が変わらない不具合を修正。
//  1.23. 0 | 単語データのコピーやペーストを行う機能を追加。
//  1.22. 0 | 同時に複数の辞書を開く機能を追加。
//  1.21. 0 | 単語を自動生成したときに前回のパラメータを保持するよう変更。
//          | ver 1.1.0 相当の Zatlin に対応。
//  1.20. 1 | OneToMany 形式の編集画面から単語生成するとエラーになる不具合を修正。
//  1.20. 0 | OneToMany 形式の編集時に同名の単語があった場合にどう処理するかを選択できるよう変更。
//          | 起動時のメインウィンドウのサイズを変更する機能を追加。
//  1.19. 0 | Zatlin を実行する機能を追加。
//          | OneToMany 形式の編集画面から単語名の自動生成ができるよう変更。
//          | ヘルプに akrantiain や Zatlin に関する説明などを追加。
//  1.18. 1 | 環境設定画面を開こうとするとエラーになる不具合を修正。
//          | OneToMany 形式で発音記号の項目名を設定したときに発音記号が 2 ヶ所に表示される不具合を修正。
//  1.18. 0 | OneToMany 形式で発音記号が記載されている内容欄の項目名を設定する機能を追加。
//  1.17. 0 | 単語を自動生成する機能を追加。
//          | 文字頻度解析において 1 文字扱いする文字列を設定できるよう変更。
//  1.16. 0 | 単語リストを印刷する機能を追加。
//          | ファイル選択画面で右側のディレクトリをクリックしてもファイル一覧が更新されない不具合を修正。
//  1.15. 1 | OneToMany 形式でアルファベット順を空欄にしても ID 順で表示されない不具合を修正。
//  1.15. 0 | 単語リストへの表示用データをメモリ上に保持するかどうかを設定する機能を追加。
//          | 単語リンクが受け付けるクリック方法を設定する機能を追加。
//  1.14. 1 | OneToMany 形式で関連語リンクをクリックしてもその単語が表示されない不具合を修正。
//          | シャレイア語辞典形式で akrantiain 実行時にエラーになることがある不具合を修正。
//  1.14. 0 | 文一括検索を行う機能を追加。
//  1.13. 0 | 文字頻度解析を行う機能を追加。
//  1.12. 3 | 辞書を開いていない状態で辞書登録しようとするとエラーになる不具合を修正。
//  1.12. 2 | 0 単語の辞書を開いて統計を見ようとするとエラーになる不具合を修正。
//          | 0 単語の OneToMany 形式辞書が開かれた状態で再起動するとエラーになる不具合を修正。
//  1.12. 1 | OneToMany 形式で変化形を編集するときに句読点の設定が無視される不具合を修正。
//  1.12. 0 | 辞書の統計情報を表示する機能を追加。 
//          | OneToMany 形式の編集時に関連語を自動的に相互参照にする機能を追加。
//          | ウィンドウを常に最前面に表示するかを設定する機能を追加。
//  1.11. 0 | シャレイア語辞典形式を OneToMany 形式に変換する機能を追加。
//          | ver 0.6.1 相当の akrantiain に対応。
//          | OneToMany 形式でデフォルトデータを設定するとエラーになる不具合を修正。
//  1.10. 0 | ver 0.5.6 相当の akrantiain に対応。
//          | OneToMany 形式で訳語や変化形の区切り記号を設定する機能を追加。
//  1. 9. 0 | 辞書形式を変換する機能を追加。
//          | akrantiain で語境界以外にマッチする規則に対応。
//  1. 8. 1 | ファイル選択画面で存在しないファイル名を指定するとエラーになる不具合を修正。
//  1. 8. 0 | OneToMany 形式とシャレイア語辞典形式に snoj データを埋め込んで発音記号を生成する機能を追加。
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