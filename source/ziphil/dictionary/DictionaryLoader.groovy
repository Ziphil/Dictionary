package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryLoader<D extends Dictionary, W extends Word> extends Task<Boolean> {

  protected D $dictionary
  protected ObservableList<W> $words = FXCollections.observableArrayList()
  protected String $path

  public DictionaryLoader(D dictionary, String path) {
    $path = path
    $dictionary = dictionary
  }

  // ファイルからデータを読み込んで、作成した単語データを $words に格納します。
  // このメソッドを実装する際は、パフォーマンスの低下を防ぐため、$dictionary.getRawWords() を介して直接単語データを辞書オブジェクトに追加しないでください。
  protected abstract Boolean load()

  protected Boolean call() {
    if ($path != null) {
      Boolean result = load()
      $dictionary.getRawWords().addAll($words)
      $dictionary.updateFirst()
      for (Word word : $words) {
        if (isCancelled()) {
          return false
        }
        word.update()
      }
      return result
    } else {
      return false
    }
  }

}