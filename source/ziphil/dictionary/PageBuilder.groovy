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

  private static final String PAGE_STYLESHEET_PATH = "resource/css/main/page.css"
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

  public Node createPage(Int pageNumber) {
    Pane page = HBox.new(COLUMN_SPACING)
    Int startColumnNumber = pageNumber * $columnSize
    Int endColumnNumber = (pageNumber + 1) * $columnSize
    for (Int i = startColumnNumber ; i < endColumnNumber ; i ++) {
      Node column = createColumn(i)
      if (column != null) {
        page.getChildren().add(column)
      }
    }
    if (!page.getChildren().isEmpty()) {
      return page
    } else {
      return null
    }
  }

  private Node createColumn(Int columnNumber) {
    if (columnNumber < $separationIndices.size()) {
      return createColumnByIndices(columnNumber)
    } else {
      return caluculateSeparationIndices(columnNumber + 1)
    }
  }

  private Node createColumnByIndices(Int columnNumber) {
    Pane mainPane = createMainPane()
    Int startIndex = (columnNumber > 0) ? $separationIndices[columnNumber - 1] : 0
    Int endIndex = $separationIndices[columnNumber]
    for (Int i = startIndex ; i < endIndex ; i ++) {
      Element word = $words[i]
      Pane pane = word.getContentPaneFactory().create(true)
      mainPane.getChildren().add(pane)
    }
    return mainPane
  }

  private Node caluculateSeparationIndices(Int endColumnNumber) {
    Int startColumnNumber = $separationIndices.size()
    Int currentIndex = (startColumnNumber > 0) ? $separationIndices[startColumnNumber - 1] : 0
    Pane lastMainPane = null
    for (Int i = startColumnNumber ; i < endColumnNumber ; i ++) {
      if (currentIndex < $endIndex) {
        Pane mainPane = createMainPane()
        Scene scene = createScene(mainPane)
        Boolean last = true
        for (Int j = currentIndex ; j < $endIndex ; j ++) {
          Element word = $words[j]
          Pane pane = word.getContentPaneFactory().create(true)
          Parent root = scene.getRoot()
          mainPane.getChildren().add(pane)
          root.applyCss()
          root.layout()
          if (mainPane.getHeight() > $pageLayout.getPrintableHeight()) {
            if (j > currentIndex) {
              mainPane.getChildren().remove(pane)
              currentIndex = j
            } else {
              currentIndex = j + 1
            }
            last = false
            break
          }
        }
        if (last) {
          currentIndex = $endIndex
        }
        $separationIndices.add(currentIndex)
        lastMainPane = mainPane
      } else {
        lastMainPane = null
      }
    }
    return lastMainPane
  }

  private Pane createMainPane() {
    VBox box = VBox.new(Measurement.rpx(3))
    Double width = ($pageLayout.getPrintableWidth() - COLUMN_SPACING * ($columnSize - 1)) / $columnSize
    URL stylesheetURL = getClass().getClassLoader().getResource(PAGE_STYLESHEET_PATH)
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

  public void setPageLayout(PageLayout pageLayout) {
    $pageLayout = pageLayout
    $separationIndices.clear()
  }

  public void setFontSize(Int fontSize) {
    $fontSize = fontSize
    $separationIndices.clear()
  }

  public void setColumnSize(Int columnSize) {
    $columnSize = columnSize
    $separationIndices.clear()
  }

}