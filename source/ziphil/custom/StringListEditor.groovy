package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class StringListEditor extends Control {

  private ObjectProperty<ObservableList<String>> $strings = SimpleObjectProperty.new(FXCollections.observableArrayList())

  public StringListEditor(ObservableList<String> strings) {
    $strings.set(strings)
  }

  public StringListEditor() {
  }

  protected Skin<StringListEditor> createDefaultSkin() {
    return StringListEditorSkin.new(this)
  }

  public ObservableList<String> getStrings() {
    return $strings.get()
  }

  public void setStrings(ObservableList<String> strings) {
    $strings.set(strings)
  }

  public ObjectProperty<ObservableList<String>> stringsProperty() {
    return $strings
  }

}