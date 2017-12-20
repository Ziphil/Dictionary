package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.ObservableList
import javafx.concurrent.Task
import ziphil.module.akrantiain.Akrantiain
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

  public void export(ExportConfig config)

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

  public ObservableList<Element> getWholeWords()

  public ObservableList<W> getWords()

  public ObservableList<W> getRawWords()

  public Consumer<SearchParameter> getOnLinkClicked()

  public void setOnLinkClicked(Consumer<SearchParameter> onLinkClicked) 

  public ControllerFactory getControllerFactory()

  public Task<?> getLoader()

  // 辞書データをファイルに保存するための Task オブジェクトを返します。
  // この辞書データがファイルへの保存に対応していない場合は、null を返します。
  // ここで返される Task オブジェクトは、save メソッドおよび saveBackup メソッドで利用されます。
  public Task<?> getSaver()

  // 辞書データを他の形式で別ファイルに保存するための Task オブジェクトを返します。
  // この辞書データが他の形式での別ファイルへの保存に対応していない場合は、null を返します。
  // ここで返される Task オブジェクトは、export メソッドで利用されます。
  public Task<?> getExporter()

  public DictionaryFactory getDictionaryFactory()

  public void setDictionaryFactory(DictionaryFactory dictionaryFactory)

  public Boolean isChanged()

}