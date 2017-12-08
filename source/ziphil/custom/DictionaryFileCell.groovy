package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import ziphil.dictionary.DictionaryFactory
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DictionaryFileCell extends FileCell {

  public DictionaryFileCell() {
    super()
  }

  protected Image selectIcon(String fileName) {
    Image icon = null
    for (DictionaryFactory factory : DictionaryFactory.FACTORIES) {
      if (fileName.endsWith("." + factory.getExtension())) {
        icon = factory.createIcon()
        break
      }
    }
    return icon
  }

}