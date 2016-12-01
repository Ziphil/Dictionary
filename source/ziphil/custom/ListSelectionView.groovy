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

  private ObjectProperty<ObservableList<T>> $sources = SimpleObjectProperty.new(FXCollections.observableArrayList())
  private ObjectProperty<ObservableList<T>> $targets = SimpleObjectProperty.new(FXCollections.observableArrayList())
  private StringProperty $sourceName = SimpleStringProperty.new("候補")
  private StringProperty $targetName = SimpleStringProperty.new("選択中")

  public ListSelectionView(ObservableList<T> sources, ObservableList<T> targets) {
    $sources.set(sources)
    $targets.set(targets)
  }

  public ListSelectionView() {
  }

  protected Skin<ListSelectionView<T>> createDefaultSkin() {
    return ListSelectionViewSkin.new(this)
  }

  public ObservableList<T> getSources() {
    return $sources.get()
  }

  public void setSources(ObservableList<T> sources) {
    $sources.set(sources)
  }

  public ObjectProperty<ObservableList<T>> sourcesProperty() {
    return $sources
  }

  public ObservableList<T> getTargets() {
    return $targets.get()
  }

  public void setTargets(ObservableList<T> targets) {
    $targets.set(targets)
  }

  public ObjectProperty<ObservableList<T>> targetsProperty() {
    return $targets
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