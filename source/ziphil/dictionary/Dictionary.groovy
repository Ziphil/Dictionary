package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic 
public interface Dictionary<W extends Word> {

  // 与えられた検索文字列で単語名から検索します。
  // strict に true が指定された場合は環境設定に応じて完全一致もしくは前方一致で検索を行い、false が指定された場合は正規表現で検索を行います。
  // また、reallyStrict に true が指定されると、環境設定に関わらず完全一致検索を行います。
  // reallyStrict の設定は、strict が true の場合のみ効果を発揮します。
  public void searchByName(String search, Boolean strict, Boolean reallyStrict)

  public void searchByName(String search, Boolean strict)

  public void searchByEquivalent(String search, Boolean strict)

  public void searchByContent(String search)

  public void searchScript(String script)

  public void shuffleWords()

  // 内部データを更新します。
  // 保持している単語データリストが変更されたときに呼び出されることが想定されています。
  public void update()

  // 内部データを更新します。
  // ファイルから辞書データを読み込んだ後に呼び出されることが想定されています。
  public void updateFirst()

  // 内部データを更新します。
  // 単語データリスト以外の個別設定などが変更されたときに呼び出されることが想定されています。
  public void updateMinimum()

  // 同じ単語データをもつ辞書オブジェクトを作成します。
  // この処理は浅いコピーを行うので、コピー後の辞書オブジェクトの各単語データはコピー前のものと同一です。
  // 同じ辞書オブジェクトに対して複数の単語リストを表示させたいときに、表示条件や表示順が同期されるのを防ぐ目的で使用できます。
  public Dictionary copy()

  public void save()

  public void saveBackup()

  public Integer hitWordSize()

  public Integer totalWordSize()

  public String getName() 

  public void setName(String name)

  public String getPath()

  public void setPath(String path)

  public ObservableList<Element> getWholeWords()

  public ObservableList<W> getWords()

  public ObservableList<W> getRawWords()

  public Task<?> getLoader()

  public Task<?> getSaver()

  public Boolean isChanged()

}