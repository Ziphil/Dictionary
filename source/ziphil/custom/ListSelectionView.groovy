package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.StringProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ListSelectionView<T> extends Control {

  private ObjectProperty<ObservableList<T>> $sourceList = SimpleObjectProperty.new(FXCollections.observableArrayList())
  private ObjectProperty<ObservableList<T>> $targetList = SimpleObjectProperty.new(FXCollections.observableArrayList())
  private StringProperty $sourceName = SimpleStringProperty.new("候補")
  private StringProperty $targetName = SimpleStringProperty.new("選択中")

  public ListSelectionView(ObservableList<T> sourceList, ObservableList<T> targetList) {
    $sourceList.set(sourceList)
    $targetList.set(targetList)
  }

  protected Skin<ListSelectionView<T>> createDefaultSkin() {
    return ListSelectionViewSkin.new(this)
  }

  public ObservableList<T> getSourceList() {
    return $sourceList.get()
  }

  public void setSourceList(ObservableList<T> sourceList) {
    $sourceList.set(sourceList)
  }

  public ObjectProperty<ObservableList<T>> sourceListProperty() {
    return $sourceList
  }

  public ObservableList<T> getTargetList() {
    return $targetList.get()
  }

  public void setTargetList(ObservableList<T> targetList) {
    $targetList.set(targetList)
  }

  public ObjectProperty<ObservableList<T>> targetListProperty() {
    return $targetList
  }

  public String getSourceName() {
    return $sourceName.get()
  }

  public void setSourceName(String sourceName) {
    $sourceName.set(sourceName)
  }

  public StringProperty sourceNameProperty() {
    return $sourceName
  }

  public String getTargetName() {
    return $targetName.get()
  }

  public void setTargetName(String targetName) {
    $targetName.set(targetName)
  }

  public StringProperty targetNameProperty() {
    return $targetName
  }

}