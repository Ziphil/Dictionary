package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum SearchMode {

  NAME("単語", KeyCodeCombination.new(KeyCode.W, KeyCombination.SHORTCUT_DOWN)),
  EQUIVALENT("訳語", KeyCodeCombination.new(KeyCode.E, KeyCombination.SHORTCUT_DOWN)),
  BOTH("両方", KeyCodeCombination.new(KeyCode.W, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)),
  CONTENT("全文", KeyCodeCombination.new(KeyCode.Q, KeyCombination.SHORTCUT_DOWN))

  private String $string = ""
  private KeyCombination $accelerator

  private SearchMode(String string, KeyCombination accelerator) {
    $string = string
    $accelerator = accelerator
  }

  public String toString() {
    return $string
  }

  public KeyCombination getAccelerator() {
    return $accelerator
  }

}