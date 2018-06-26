package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.ObservableList
import javafx.concurrent.Task
import ziphil.module.akrantiain.Akrantiain
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface Dictionary<W extends Word, F extends DictionaryFactory> {

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

  // この単語を saver に渡されたセーバーによってファイルに保存します。
  // saver に保存先のパスが指定されていない場合は、この辞典データが保持しているパスに保存します。
  // なお、実際にデータをファイルに保存する際は、このメソッドを直接呼び出すのではなく DictionaryFactory 経由で行うことが推奨されています。
  public void save(Saver saver)

  public Int hitWordSize()

  public Int totalWordSize()

  public IndividualSetting createIndividualSetting()

  public String getName() 

  public void setName(String name)

  public String getPath()

  public void setPath(String path)

  // アルファベット順を格納した文字列を返します。
  // この辞書データがアルファベット順の情報をもっていない場合は、null を返します。
  public String getAlphabetOrder()

  // 埋め込み akrantiain のオブジェクトを返します。
  // この辞書データが埋め込み akrantiain に対応していない場合は、null を返します。
  public Akrantiain getAkrantiain()

  public ObservableList<Element> getElements()

  public ObservableList<W> getWords()

  public ObservableList<W> getRawWords()

  public List<W> getRawSortedWords()

  public Consumer<SearchParameter> getOnLinkClicked()

  public void setOnLinkClicked(Consumer<SearchParameter> onLinkClicked) 

  public Task<?> getLoader()

  // 辞書データをファイルに保存するための Task オブジェクトを返します。
  // ここで返される Task オブジェクトは、save メソッドが呼び出されたときに作成されます。
  public Task<?> getSaver()

  public F getDictionaryFactory()

  public void setDictionaryFactory(F dictionaryFactory)

  public Boolean isChanged()

}