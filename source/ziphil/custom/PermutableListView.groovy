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
public class PermutableListView<T> extends Control {

  private ObjectProperty<ObservableList<T>> $items = SimpleObjectProperty.new(FXCollections.observableArrayList())

  public PermutableListView(ObservableList<T> items) {
    $items.set(items)
  }

  public PermutableListView() {
  }

  protected Skin<PermutableListView<T>> createDefaultSkin() {
    return PermutableListViewSkin.new(this)
  }

  public ObservableList<T> getItems() {
    return $items.get()
  }

  public void setItems(ObservableList<T> items) {
    $items.set(items)
  }

  public ObjectProperty<ObservableList<T>> itemsProperty() {
    return $items
  }

}