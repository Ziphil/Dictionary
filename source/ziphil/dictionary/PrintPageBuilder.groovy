package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.print.PageLayout
import ziphil.custom.Measurement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PrintPageBuilder {

  private static final String PRINT_STYLESHEET_PATH = "resource/css/main/print.css"
  private static final Double COLUMN_SPACING = Measurement.rpx(15)

  private List<Element> $words
  private Int $currentIndex = 0
  private Int $startIndex = 0
  private Int $endIndex = 0
  private Int $currentPageNumber = 0
  private PageLayout $pageLayout
  private Int $fontSize = 10
  private Int $columnSize = 1

  public PrintPageBuilder(List<Element> words, Int startIndex, Int endIndex) {
    $words = words
    $currentIndex = startIndex
    $startIndex = startIndex
    $endIndex = endIndex
  }

  public Node nextPage() {
    Double width = ($pageLayout.getPrintableWidth() - COLUMN_SPACING * ($columnSize - 1)) / $columnSize
    Pane page = HBox.new(COLUMN_SPACING)
    Boolean hasPage = true
    for (Int i = 0 ; i < $columnSize ; i ++) {
      Node column = nextColumn(width)
      if (column != null) {
        page.getChildren().add(column)
      } else {
        if (i == 0) {
          hasPage = false
        }
        break
      }
    }
    if (hasPage) {
      $currentPageNumber ++
      return page
    } else {
      return null
    }
  }

  private Node nextColumn(Double width) {
    if ($currentIndex < $endIndex) {
      Pane mainPane = createMainPane(width)
      Scene scene = createScene(mainPane)
      Boolean last = true
      for (Int i = $currentIndex ; i < $endIndex ; i ++) {
        Element word = $words[i]
        Pane pane = word.getContentPaneFactory().create(true)
        Parent root = scene.getRoot()
        mainPane.getChildren().add(pane)
        root.applyCss()
        root.layout()
        if (mainPane.getHeight() > $pageLayout.getPrintableHeight()) {
          if (i > $currentIndex) {
            mainPane.getChildren().remove(pane)
            $currentIndex = i
          } else {
            $currentIndex = i + 1
          }
          last = false
          break
        }
      }
      if (last) {
        $currentIndex = $endIndex
      }
      return scene.getRoot()
    } else {
      return null
    }
  }

  private Pane createMainPane(Double width) {
    VBox box = VBox.new(Measurement.rpx(3))
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

  public void reset() {
    $currentIndex = $startIndex
  }

  public Int getCurrentPageNumber() {
    return $currentPageNumber
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