package ziphil.node

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import ziphil.dictionary.Word


@CompileStatic @Newify
public class WordCell extends ListCell<Word> {

  public WordCell() {
    super()
  }

  protected void updateItem(Word word, boolean isEmpty) {
    super.updateItem(word, isEmpty)
    if (isEmpty || word == null) {
      setText(null)
      setGraphic(null)
    } else {
      word.getContentPane().prefWidthProperty().bind(getListView().widthProperty().subtract(29))
      setText(null)
      setGraphic(word.getContentPane())
    }
  }

}