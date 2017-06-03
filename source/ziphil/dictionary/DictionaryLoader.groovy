package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryLoader<D extends Dictionary, W extends Word> extends Task<BooleanClass> {

  protected D $dictionary
  protected String $path
  protected List<W> $words = ArrayList.new()

  public DictionaryLoader(D dictionary, String path) {
    $path = path
    $dictionary = dictionary
    updateProgress(0, 1)
  }

  // ファイルからデータを読み込んで、作成した単語データを $words に格納します。
  // このメソッドを実装する際は、パフォーマンスの低下を防ぐため、$dictionary.getRawWords() を介して直接単語データを辞書オブジェクトに追加しないでください。
  protected abstract BooleanClass load()

  protected BooleanClass call() {
    if ($path != null) {
      BooleanClass result = load()
      updateProgress(1, 1)
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