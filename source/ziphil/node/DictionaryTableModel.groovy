package ziphil.node

import groovy.transform.CompileStatic
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty


@CompileStatic @Newify
public class DictionaryTableModel {

  private StringProperty $name = SimpleStringProperty.new()
  private StringProperty $type = SimpleStringProperty.new()
  private IntegerProperty $wordSize = SimpleIntegerProperty.new()
  private IntegerProperty $fileSize = SimpleIntegerProperty.new()
  private StringProperty $path = SimpleStringProperty.new()

  public DictionaryTableModel(String name, String type, String path) {
    $name.set(name)
    $type.set(type)
    $wordSize.set(0)
    $fileSize.set(0)
    $path.set(path)
  }

  public String getName() {
    return $name.get()
  }

  public StringProperty nameProperty() {
    return $name
  }

  public String getType() {
    return $type.get()
  }

  public StringProperty typeProperty() {
    return $type
  }

  public Integer getWordSize() {
    return $wordSize.get()
  }

  public IntegerProperty wordSizeProperty() {
    return $wordSize
  }

  public Integer getFileSize() {
    return $fileSize.get()
  }

  public IntegerProperty fileSizeProperty() {
    return $fileSize
  }

  public String getPath() {
    return $path.get()
  }

  public StringProperty pathProperty() {
    return $path
  }

}