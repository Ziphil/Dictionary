package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import ziphil.dictionary.Word
import ziphilib.transform.ConvertPrimitive


@CompileStatic @Newify
public class WordCell extends ListCell<Word> {

  public WordCell() {
    super()
  }

  @ConvertPrimitive
  protected void updateItem(Word word, Boolean isEmpty) {
    super.updateItem(word, isEmpty)
    if (isEmpty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      if (word.isChanged()) {
        word.createContentPane()
      }
      word.getContentPane().prefWidthProperty().bind(getListView().widthProperty().subtract(29))
      setText(null)
      setGraphic(word.getContentPane())
    }
  }

}