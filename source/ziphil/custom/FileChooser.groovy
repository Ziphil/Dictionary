package ziphil.custom

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class FileChooser extends Control {

  private ObjectProperty<File> $currentDirectory = SimpleObjectProperty.new()
  private ObjectProperty<ExtensionFilter> $currentFileType = SimpleObjectProperty.new()
  private ReadOnlyObjectWrapper<File> $selectedFile = ReadOnlyObjectWrapper.new()
  private ListProperty<ExtensionFilter> $extensionFilters = SimpleListProperty.new(FXCollections.observableArrayList())
  private BooleanProperty $showsHidden = SimpleBooleanProperty.new(false)
  private BooleanProperty $adjustsExtension = SimpleBooleanProperty.new(false)

  public FileChooser() {
  }

  protected Skin<FileChooser> createDefaultSkin() {
    return FileChooserSkin.new(this, $selectedFile)
  }

  public File getCurrentDirectory() {
    return $currentDirectory.get()
  }

  public void setCurrentDirectory(File currentDirectory) {
    $currentDirectory.set(currentDirectory)
  }

  public ObjectProperty<File> currentDirectoryProperty() {
    return $currentDirectory
  }

  public ExtensionFilter getCurrentFileType() {
    return $currentFileType.get()
  }

  public void setCurrentFileType(ExtensionFilter currentFileType) {
    $currentFileType.set(currentFileType)
  }

  public ObjectProperty<ExtensionFilter> currentFileTypeProperty() {
    return $currentFileType
  }

  public File getSelectedFile() {
    return $selectedFile.get()
  }

  public ReadOnlyObjectProperty<File> selectedFileProperty() {
    return $selectedFile.getReadOnlyProperty()
  }

  public ObservableList<ExtensionFilter> getExtensionFilters() {
    return $extensionFilters.getValue()
  }

  public void setExtensionFilters(ObservableList<ExtensionFilter> extensionFilters) {
    $extensionFilters.setValue(extensionFilters)
  }

  public ListProperty<ExtensionFilter> extensionFiltersProperty() {
    return $extensionFilters
  }

  public Boolean isShowsHidden() {
    return $showsHidden.get()
  }

  public void setShowsHidden(Boolean showsHidden) {
    $showsHidden.set(showsHidden)
  }

  public BooleanProperty showsHiddenProperty() {
    return $showsHidden
  }

  public Boolean isAdjustsExtension() {
    return $adjustsExtension.get()
  }

  public void setAdjustsExtension(Boolean adjustsExtension) {
    $adjustsExtension.set(adjustsExtension)
  }

  public BooleanProperty adjustsExtensionProperty() {
    return $adjustsExtension
  }

}