package ziphil.custom

import groovy.transform.CompileStatic
import java.util.function.UnaryOperator
import javafx.scene.control.TextFormatter.Change


@CompileStatic @Newify
public class IntegerUnaryOperator implements UnaryOperator<Change> {

  public Change apply(Change change) {
    if (change.isAdded()) {
      String text = change.getText()
      String controlText = change.getControlText()
      StringBuilder result = StringBuilder.new()
      Boolean hasSign
      if (change.isReplaced()) {
        if (change.getRangeStart() == 0) {
          hasSign = change.getRangeEnd() < controlText.length() && change.getControlText()[change.getRangeEnd()] == "-"
        } else {
          hasSign = controlText.startsWith("-")
        }
      } else {
        hasSign = controlText.startsWith("-")
      }
      (0 ..< text.length()).each() { Integer i ->
        String character = text[i]
        if (hasSign) {
          if (change.getRangeStart() != 0 && character >= "0" && character <= "9") {
            result.append(character)
          }
        } else {
          if (character >= "0" && character <= "9") {
            result.append(character)
          }
          if (change.getRangeStart() == 0 && character == "-") {
            result.append(character)
          }
        }
      }
      Integer anchor = change.getAnchor() - text.length() + result.length()
      Integer caretPosition = change.getCaretPosition() - text.length() + result.length()
      change.setText(result.toString())
      change.setAnchor(anchor)
      change.setCaretPosition(caretPosition)
    }
    return change
  }

}