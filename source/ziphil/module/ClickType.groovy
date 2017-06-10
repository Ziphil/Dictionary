package ziphil.module

import groovy.transform.CompileStatic
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum ClickType {

  PRIMARY("シングル左クリック"),
  DOUBLE_PRIMARY("ダブル左クリック"),
  MIDDLE("中央ボタンクリック"),
  SHORTCUT_PRIMARY("Shortcut＋左クリック")

  private String $string = ""

  private ClickType(String string) {
    $string = string
  }

  public Boolean matches(MouseEvent event) {
    if (this == PRIMARY) {
      return event.getButton() == MouseButton.PRIMARY
    } else if (this == DOUBLE_PRIMARY) {
      return event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
    } else if (this == MIDDLE) {
      return event.getButton() == MouseButton.MIDDLE
    } else if (this == SHORTCUT_PRIMARY) {
      return event.getButton() == MouseButton.PRIMARY && event.isShortcutDown()
    }
  }

  public String toString() {
    return $string
  }

}