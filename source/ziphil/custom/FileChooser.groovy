package ziphil.custom

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.SortedList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.control.TreeView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox


@CompileStatic @Newify
public class FileChooser extends VBox {

  private static final String RESOURCE_PATH = "resource/fxml/file_chooser.fxml"
  private static final Comparator<File> FILE_COMPARATOR = createFileComparator()

  @FXML private TreeView<File> $directoryTree
  @FXML private ListView<File> $fileList
  @FXML private SplitPane $splitPane
  @FXML private TextField $directory
  @FXML private TextField $file
  private BooleanProperty $showsHidden = SimpleBooleanProperty.new(false)
  private ObjectProperty<ObservableList<File>> $currentFiles = SimpleObjectProperty.new()
  private ObjectProperty<File> $currentDirectory = SimpleObjectProperty.new()
  private ObjectProperty<File> $currentFile = SimpleObjectProperty.new()
  private ReadOnlyObjectWrapper<File> $selectedFile = ReadOnlyObjectWrapper.new()

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
    bindSelectedFile()
  }

  private void changeCurrentFile(File file) {
    if (file != null && file.isFile()) {
      $file.setText(file.getName())
      $currentFile.set(file)
    }
  }

  private void changeCurrentDirectory(File file) {
    if (file != null) {
      if (file.isDirectory()) {
        $directory.setText(file.toString())
        $currentDirectory.set(file)
      } else if (file.isFile()) {
        $file.setText(file.getName())
        $currentFile.set(file)
      }
      $fileList.scrollTo(0)
    }
  }

  private void changeCurrentDirectoryToHome() {
    String homePath = System.getProperty("user.home")
    File home = File.new(homePath)
    if (home.isDirectory()) {
      $directory.setText(home.toString())
      $currentDirectory.set(home)
    }
    $fileList.scrollTo(0)
  }

  private void setupDirectoryTree() {
    DirectoryItem root = DirectoryItem.new(null)
    File.listRoots().each() { File file ->
      root.getChildren().add(DirectoryItem.new(file))
    }
    $directoryTree.setRoot(root)
    $directoryTree.setShowRoot(false)
    $directoryTree.setCellFactory() { TreeView<File> tree ->
      DirectoryCell cell = DirectoryCell.new()
      cell.setOnMouseClicked() { MouseEvent event ->
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          changeCurrentDirectory(cell.getItem())
        }
      }
      return cell
    }
  }

  private void setupFileList() {
    $fileList.setCellFactory() { ListView<File> list ->
      FileCell cell = FileCell.new()
      cell.setOnMouseClicked() { MouseEvent event ->
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
              files.add(innerFile)
            }
          }
        }
      }
      SortedList sortedFiles = SortedList.new(files, FILE_COMPARATOR)
      return sortedFiles
    }
    ObjectBinding<ObservableList<File>> binding = Bindings.createObjectBinding(function, $currentDirectory, $showsHidden)
    $fileList.itemsProperty().bind(binding)
  }

  private void setupSplitPane() {
    $splitPane.setDividerPositions(0.3)
  }

  private void setupDirectory() {
    $directory.setOnAction() {
      File file = File.new($directory.getText())
      changeCurrentDirectory(file)
    }
  }

  private void setupFile() {
    Platform.runLater() {
      $file.requestFocus()
    }
  }

  private void bindSelectedFile() {
    Callable<File> function = (Callable){
      File directory = $currentDirectory.get()
      if (directory != null) {
        String filePath = directory.toString() + File.separator + $file.getText()
        File file = File.new(filePath)
        return file
      } else {
        return null
      }
    }
    ObjectBinding<File> binding = Bindings.createObjectBinding(function, $currentDirectory, $file.textProperty())
    $selectedFile.bind(binding)
  }

  private void loadResource() {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(RESOURCE_PATH))
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

  public Boolean showsHidden() {
    return $showsHidden.get()
  }

  public void setShowsHidden(Boolean showsHidden) {
    $showsHidden.set(showsHidden)
  }

  public BooleanProperty showsHiddenProperty() {
    return $showsHidden
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

}