package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HelpIndicator extends Control {

  private StringProperty $text = SimpleStringProperty.new("")
  private StringProperty $markCharacter = SimpleStringProperty.new("?")

  public HelpIndicator(String text) {
    $text.set(text)
  }

  public HelpIndicator() {
  }

  protected Skin<HelpIndicator> createDefaultSkin() {
    return HelpIndicatorSkin.new(this)
  }

  public String getText() {
    return $text.get()
  }

  public void setText(String text) {
    $text.set(text)
  }

  public StringProperty textProperty() {
    return $text
  }

  public String getMarkCharacter() {
    return $markCharacter.get()
  }

  public void setMarkCharacter(String markCharacter) {
    $markCharacter.set(markCharacter)
  }

  public StringProperty markCharacterProperty() {
    return $markCharacter
  }

}