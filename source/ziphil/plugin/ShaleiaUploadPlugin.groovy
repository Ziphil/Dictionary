package ziphil.plugin

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import ziphil.dictionary.Dictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaUploadPlugin implements Plugin {

  private static final String NAME = "データアップロード"

  public void call(Dictionary dictionary) {
  }

  public Boolean isSupported(Dictionary dictionary) {
    if (dictionary instanceof ShaleiaDictionary) {
      if (dictionary.getVersion() == "5.5") {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }

  public String getName() {
    return NAME
  }

  public KeyCode getKeyCode() {
    return KeyCode.U
  }

  public Image getIcon() {
    return null
  }

}