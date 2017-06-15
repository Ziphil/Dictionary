package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.print.PageLayout
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import ziphil.custom.Measurement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PageBuilder {

  private static final String PRINT_STYLESHEET_PATH = "resource/css/main/print.css"
  private static final Double COLUMN_SPACING = Measurement.rpx(15)

  private List<Element> $words
  private Int $startIndex = 0
  private Int $endIndex = 0
  private List<IntegerClass> $separationIndices = ArrayList.new()
  private PageLayout $pageLayout
  private Int $fontSize = 10
  private Int $columnSize = 1

  public PageBuilder(List<Element> words, Int startIndex, Int endIndex) {
    $words = words
    $startIndex = startIndex
    $endIndex = endIndex
  }

  public void prepare() {
    Int currentIndex = $startIndex
    while (currentIndex < $endIndex) {
      Pane mainPane = createMainPane()
      Scene scene = createScene(mainPane)
      Boolean last = true
      for (Int i = currentIndex ; i < $endIndex ; i ++) {
        Element word = $words[i]
        Pane pane = word.getContentPaneFactory().create(true)
        Parent root = scene.getRoot()
        mainPane.getChildren().add(pane)
        root.applyCss()
        root.layout()
        if (mainPane.getHeight() > $pageLayout.getPrintableHeight()) {
          if (i > currentIndex) {
            mainPane.getChildren().remove(pane)
            currentIndex = i
          } else {
            currentIndex = i + 1
          }
          last = false
          break
        }
      }
      if (last) {
        currentIndex = $endIndex
      }
      $separationIndices.add(currentIndex - 1)
    }
  }

  public Node createPage(Int pageNumber) {
    if (pageNumber >= 0 && pageNumber < pageSize()) {
      Pane page = HBox.new(COLUMN_SPACING)
      Int startColumnNumber = pageNumber * $columnSize
      Int endColumnNumber = (pageNumber + 1) * $columnSize
      for (Int i = startColumnNumber ; i < endColumnNumber ; i ++) {
        Node column = createColumn(i)
        if (column != null) {
          page.getChildren().add(column)
        }
      }
      return page
    } else {
      return null
    }
  }

  private Node createColumn(Int columnNumber) {
    if (columnNumber >= 0 && columnNumber < columnSize()) {
      Pane mainPane = createMainPane()
      Int startIndex = (columnNumber > 0) ? $separationIndices[columnNumber - 1] + 1 : 0
      Int endIndex = $separationIndices[columnNumber] + 1
      for (Int i = startIndex ; i < endIndex ; i ++) {
        Element word = $words[i]
        Pane pane = word.getContentPaneFactory().create(true)
        mainPane.getChildren().add(pane)
      }
      return mainPane
    } else {
      return null
    }
  }

  private Pane createMainPane() {
    VBox box = VBox.new(Measurement.rpx(3))
    Double width = ($pageLayout.getPrintableWidth() - COLUMN_SPACING * ($columnSize - 1)) / $columnSize
    URL stylesheetURL = getClass().getClassLoader().getResource(PRINT_STYLESHEET_PATH)
    box.setPrefWidth(width)
    box.getStylesheets().add(stylesheetURL.toString())
    StringBuilder style = StringBuilder.new()
    style.append("-fx-font-size: ")
    style.append($fontSize)
    style.append(";")
    box.setStyle(style.toString())
    return box
  }

  private Scene createScene(Node node) {
    Group group = Group.new()
    Scene scene = Scene.new(group)
    group.getChildren().add(node)
    return scene
  }

  private Int columnSize() {
    return $separationIndices.size()
  }

  public Int pageSize() {
    return (columnSize() > 0) ? (columnSize() - 1).intdiv($columnSize) + 1 : 0
  }

  public void setPageLayout(PageLayout pageLayout) {
    $pageLayout = pageLayout
  }

  public void setFontSize(Int fontSize) {
    $fontSize = fontSize
  }

  public void setColumnSize(Int columnSize) {
    $columnSize = columnSize
  }

}