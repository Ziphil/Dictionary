package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.ObservableList
import javafx.concurrent.Task
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface Dictionary<W extends Word> {

  public void search(SearchParameter parameter)

  public void shuffleWords()

  // 内部データを更新します。
  // ファイルから辞書データを読み込んだ後に呼び出されることが想定されています。
  public void updateFirst()

  // 内部データを更新します。
  // 単語データリスト以外の個別設定などが変更されたときに呼び出されることが想定されています。
  public void updateMinimum()

  public Object createPlainWord(W word)

  // 同じ単語データをもつ辞書オブジェクトを作成します。
  // この処理は浅いコピーを行うので、コピー後の辞書オブジェクトの各単語データはコピー前のものと同一です。
  // 同じ辞書オブジェクトに対して複数の単語リストを表示させたいときに、表示条件や表示順が同期されるのを防ぐ目的で使用できます。
  public Dictionary copy()

  public void save()

  public void saveBackup()

  public Int hitWordSize()

  public Int totalWordSize()

  public String getName() 

  public void setName(String name)

  public String getPath()

  public void setPath(String path)

  public ObservableList<Element> getWholeWords()

  public ObservableList<W> getWords()

  public ObservableList<W> getRawWords()

  public Consumer<SearchParameter> getOnLinkClicked()

  public void setOnLinkClicked(Consumer<SearchParameter> onLinkClicked) 

  public ControllerSupplier getControllerSupplier()

  public Task<?> getLoader()

  public Task<?> getSaver()

  public Boolean isChanged()

}