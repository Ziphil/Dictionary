package ziphil.custom

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.SortedList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.control.TreeView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ziphilib.transform.ReturnVoidClosure


@CompileStatic @Newify
public class FileChooser extends VBox {

  private static final String RESOURCE_PATH = "resource/fxml/file_chooser.fxml"
  private static final Comparator<File> FILE_COMPARATOR = createFileComparator()
  private static final ExtensionFilter DEFAULT_EXTENSION_FILTER = ExtensionFilter.new("全てのファイル", null)

  @FXML private TreeView<File> $directoryTree
  @FXML private ListView<File> $fileList
  @FXML private SplitPane $splitPane
  @FXML private TextField $directoryControl
  @FXML private TextField $fileControl
  @FXML private ComboBox<ExtensionFilter> $fileTypeControl
  private BooleanProperty $showsHidden = SimpleBooleanProperty.new(false)
  private BooleanProperty $adjustsExtension = SimpleBooleanProperty.new(false)
  private ObjectProperty<ObservableList<File>> $currentFiles = SimpleObjectProperty.new()
  private ObjectProperty<File> $currentDirectory = SimpleObjectProperty.new()
  private ObjectProperty<File> $currentFile = SimpleObjectProperty.new()
  private ReadOnlyObjectWrapper<File> $selectedFile = ReadOnlyObjectWrapper.new()
  private ListProperty<ExtensionFilter> $extensionFilters = SimpleListProperty.new(FXCollections.observableArrayList())

  public FileChooser() {
    loadResource()
    changeCurrentDirectoryToHome()
  }

  @FXML
  private void initialize() {
    setupDirectoryTree()
    setupFileList()
    setupDirectory()
    setupFile()
    setupSplitPane()
    setupFileTypes()
    bindSelectedFile()
  }

  private void changeCurrentFile(File file) {
    if (file != null && file.isFile()) {
      $fileControl.setText(file.getName())
      $currentFile.set(file)
    }
  }

  private void changeCurrentDirectory(File file) {
    if (file != null) {
      if (file.isDirectory()) {
        $directoryControl.setText(file.getAbsolutePath())
        $currentDirectory.set(file)
        $fileList.scrollTo(0)
      } else if (file.isFile()) {
        $fileControl.setText(file.getName())
        $currentFile.set(file)
      }
    }
  }

  @FXML
  private void changeCurrentDirectoryToHome() {
    String homePath = System.getProperty("user.home")
    File home = File.new(homePath)
    if (home.isDirectory()) {
      $directoryControl.setText(home.getAbsolutePath())
      $currentDirectory.set(home)
      $fileList.scrollTo(0)
    }
  }

  @FXML
  private void changeCurrentDirectoryToParent() {
    File parent = $currentDirectory.get().getParentFile()
    if (parent != null) {
      $directoryControl.setText(parent.getAbsolutePath())
      $currentDirectory.set(parent)
      $fileList.scrollTo(0)
    }
  }

  @ReturnVoidClosure
  private void setupDirectoryTree() {
    DirectoryItem root = DirectoryItem.new(null)
    File.listRoots().each() { File file ->
      root.getChildren().add(DirectoryItem.new(file))
    }
    $directoryTree.setRoot(root)
    $directoryTree.setShowRoot(false)
    $directoryTree.setCellFactory() { TreeView<File> tree ->
      DirectoryCell cell = DirectoryCell.new()
      cell.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          changeCurrentDirectory(cell.getItem())
        }
      }
      return cell
    }
  }

  @ReturnVoidClosure
  private void setupFileList() {
    $fileList.setCellFactory() { ListView<File> list ->
      FileCell cell = FileCell.new()
      cell.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
          changeCurrentFile(cell.getItem())
        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          changeCurrentDirectory(cell.getItem())
        }
      }
      return cell
    }
    Callable<ObservableList<File>> function = (Callable){
      File directory = $currentDirectory.get()
      ObservableList<File> files = FXCollections.observableArrayList()
      if (directory != null) {
        File[] innerFiles = $currentDirectory.get().listFiles()
        if (innerFiles != null) {
          innerFiles.each() { File innerFile ->
            if ($showsHidden.get() || !innerFile.isHidden()) {
              if (innerFile.isDirectory() || $fileTypeControl.getValue().accepts(innerFile)) {
                files.add(innerFile)
              }
            }
          }
        }
      }
      SortedList sortedFiles = SortedList.new(files, FILE_COMPARATOR)
      return sortedFiles
    }
    ObjectBinding<ObservableList<File>> binding = Bindings.createObjectBinding(function, $currentDirectory, $showsHidden, $fileTypeControl.valueProperty())
    $fileList.itemsProperty().bind(binding)
  }

  private void setupSplitPane() {
    $splitPane.setDividerPositions(0.3)
  }

  private void setupDirectory() {
    $directoryControl.setOnAction() {
      File file = File.new($directoryControl.getText())
      changeCurrentDirectory(file)
    }
  }

  private void setupFile() {
    Platform.runLater() {
      $fileControl.requestFocus()
    }
  }

  private void setupFileTypes() {
    Callable<ObservableList<ExtensionFilter>> function = (Callable){
      ObservableList<ExtensionFilter> items = FXCollections.observableArrayList()
      items.add(DEFAULT_EXTENSION_FILTER)
      items.addAll($extensionFilters.getValue())
      return items
    }
    ObjectBinding<ObservableList<ExtensionFilter>> binding = Bindings.createObjectBinding(function, $extensionFilters)
    $fileTypeControl.itemsProperty().bind(binding)
    $fileTypeControl.getSelectionModel().selectFirst()
  }

  private void bindSelectedFile() {
    Callable<File> function = (Callable){
      File directory = $currentDirectory.get()
      if (directory != null) {
        String filePath = directory.getAbsolutePath() + File.separator + $fileControl.getText()
        if ($adjustsExtension.get()) {
          String additionalExtension = $fileTypeControl.getValue().getExtension()
          if (additionalExtension != null) {
            if (!filePath.endsWith("." + additionalExtension)) {
              filePath = filePath + "." + additionalExtension
            }
          }
        }
        File file = File.new(filePath)
        return file
      } else {
        return null
      }
    }
    ObjectBinding<File> binding = Bindings.createObjectBinding(function, $currentDirectory, $fileControl.textProperty(), $adjustsExtension, $fileTypeControl.valueProperty())
    $selectedFile.bind(binding)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH), null, CustomBuilderFactory.new())
    loader.setRoot(this)
    loader.setController(this)
    loader.load()
  }

  private static Comparator<File> createFileComparator() {
    Comparator<File> comparator = (Comparator){ File firstFile, File secondFile ->
      if (firstFile.isDirectory()) {
        if (secondFile.isDirectory()) {
          return firstFile.getName().compareToIgnoreCase(secondFile.getName())
        } else {
          return -1
        }
      } else {
        if (secondFile.isDirectory()) {
          return 1
        } else {
          return firstFile.getName().compareToIgnoreCase(secondFile.getName())
        }
      }
    }
    return comparator
  }

  public ComboBox<ExtensionFilter> getFileTypeControl() {
    return $fileTypeControl
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

  public File getCurrentDirectory() {
    return $currentDirectory.get()
  }

  public void setCurrentDirectory(File directory) {
    $currentDirectory.set(directory)
  }

  public ObjectProperty<File> currentDirectoryProperty() {
    return $currentDirectory
  }

  public File getCurrentFile() {
    return $currentFile.get()
  }

  public void setCurrentFile(File directory) {
    $currentFile.set(directory)
  }

  public ObjectProperty<File> currentFileProperty() {
    return $currentFile
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

}