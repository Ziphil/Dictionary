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

  private ObjectProperty<File> $currentDirectory = SimpleObjectProperty.new(null)
  private ObjectProperty<ExtensionFilter> $currentFileType = SimpleObjectProperty.new(null)
  private StringProperty $inputtedFileName = SimpleStringProperty.new("")
  private ReadOnlyObjectWrapper<File> $selectedFile = ReadOnlyObjectWrapper.new()
  private ListProperty<ExtensionFilter> $extensionFilters = SimpleListProperty.new(FXCollections.observableArrayList())
  private BooleanProperty $showsHidden = SimpleBooleanProperty.new(false)
  private BooleanProperty $adjustsExtension = SimpleBooleanProperty.new(false)

  public FileChooser() {
    bindSelectedFile()
  }

  public void moveToHome() {
    String homePath = System.getProperty("user.home")
    File home = File.new(homePath)
    if (home.isDirectory()) {
      $currentDirectory.set(home)
    }
  }

  public void moveToParent() {
    File parent = $currentDirectory.get().getParentFile()
    if (parent != null) {
      $currentDirectory.set(parent)
    }
  }

  private void bindSelectedFile() {
    Callable<File> function = (Callable){
      File directory = $currentDirectory.get()
      if (directory != null) {
        String filePath = directory.getAbsolutePath() + File.separator + $inputtedFileName.get()
        if ($adjustsExtension.get() && $currentFileType.get() != null) {
          String additionalExtension = $currentFileType.get().getExtension()
          if (additionalExtension != null) {
            if (!filePath.endsWith("." + additionalExtension)) {
              filePath = filePath + "." + additionalExtension
            }
          }
        }
        File file = File.new(filePath)
        if (file.isFile()) {
          return file
        } else {
          return null
        }
      } else {
        return null
      }
    }
    ObjectBinding<File> binding = Bindings.createObjectBinding(function, $currentDirectory, $inputtedFileName, $currentFileType, $adjustsExtension)
    $selectedFile.bind(binding)
  }

  protected Skin<FileChooser> createDefaultSkin() {
    return FileChooserSkin.new(this)
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

  public String getInputtedFileName() {
    return $inputtedFileName.get()
  }

  public void setInputtedFileName(String inputtedFileName) {
    $inputtedFileName.set(inputtedFileName)
  }

  public StringProperty inputtedFileNameProperty() {
    return $inputtedFileName
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