package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.control.Button
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class UnfocusableButton extends Button {

  public UnfocusableButton() {
    super()
    setFocusTraversable(false)
  }

  public UnfocusableButton(String text) {
    super(text)
    setFocusTraversable(false)
  }

  public UnfocusableButton(String text, Node graphic) {
    super(text, graphic)
    setFocusTraversable(false)
  }

  public void requestFocus() {
  }

}