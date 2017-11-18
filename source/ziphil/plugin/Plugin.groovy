package ziphil.plugin

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryType
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface Plugin {

  public void call(Dictionary dictionary)

  // このプラグインが dictionary の操作に対応していれば true を返し、対応していなければ false を返します。
  // 対応していない辞書が開かれている場合は、メニューにこのプラグインを実行する項目が表示されません。
  public Boolean isSupported(Dictionary dictionary)

  public String getName()

  // ショートカットキー用のキーコードを返します。
  // すでに使われているキーとの衝突を防ぐため、実際のショートカットキーは Command＋Alt＋このメソッドが返すキーコードとなります。
  // このメソッドが null を返した場合は、ショートカットキーの設定が行われません。
  public KeyCode getKeyCode()

  public Image getIcon()

}