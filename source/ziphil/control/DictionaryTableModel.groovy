package ziphil.control

import groovy.transform.CompileStatic
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty


@CompileStatic @Newify
public class DictionaryTableModel {

  private StringProperty $nameProperty = SimpleStringProperty.new()
  private StringProperty $typeProperty = SimpleStringProperty.new()
  private IntegerProperty $wordSizeProperty = SimpleIntegerProperty.new()
  private IntegerProperty $fileSizeProperty = SimpleIntegerProperty.new()
  private StringProperty $pathProperty = SimpleStringProperty.new()

  public DictionaryTableModel(String name, String type, String path) {
    $nameProperty.set(name)
    $typeProperty.set(type)
    $wordSizeProperty.set(0)
    $fileSizeProperty.set(0)
    $pathProperty.set(path)
  }

  public String getName() {
    return $nameProperty.get()
  }

  public StringProperty nameProperty() {
    return $nameProperty
  }

  public String getType() {
    return $typeProperty.get()
  }

  public StringProperty typeProperty() {
    return $typeProperty
  }

  public Integer getWordSize() {
    return $wordSizeProperty.get()
  }

  public IntegerProperty wordSizeProperty() {
    return $wordSizeProperty
  }

  public Integer getFileSize() {
    return $fileSizeProperty.get()
  }

  public IntegerProperty fileSizeProperty() {
    return $fileSizeProperty
  }

  public String getPath() {
    return $pathProperty.get()
  }

  public StringProperty pathProperty() {
    return $pathProperty
  }

}