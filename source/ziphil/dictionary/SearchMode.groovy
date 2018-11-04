package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum SearchMode {

  NAME("単語", true, KeyCodeCombination.new(KeyCode.W, KeyCombination.SHORTCUT_DOWN)),
  EQUIVALENT("訳語", true, KeyCodeCombination.new(KeyCode.E, KeyCombination.SHORTCUT_DOWN)),
  BOTH("両方", true, KeyCodeCombination.new(KeyCode.W, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)),
  CONTENT("全文", false, KeyCodeCombination.new(KeyCode.Q, KeyCombination.SHORTCUT_DOWN))

  private String $string = ""
  private Boolean $strictable
  private KeyCombination $accelerator

  private SearchMode(String string, Boolean strictable, KeyCombination accelerator) {
    $string = string
    $strictable = strictable
    $accelerator = accelerator
  }

  public String toString() {
    return $string
  }

  public Boolean isStrictable() {
    return $strictable
  }

  public KeyCombination getAccelerator() {
    return $accelerator
  }

}