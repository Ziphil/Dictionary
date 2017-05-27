package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic 
public interface Dictionary<W extends Word> {

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