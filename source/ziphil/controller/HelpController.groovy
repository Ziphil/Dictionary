package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Desktop
import javafx.fxml.FXML
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLAnchorElement
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HelpController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/help.fxml"
  private static final String TITLE = "ヘルプ"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(640)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private ListView<HelpItem> $sectionView
  @FXML private WebView $helpView

  public HelpController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupSectionView()
    setupHelpView()
  }

  private void changeHelp(HelpItem item) {
    String path = item.getAbsolutePath()
    String url = getClass().getClassLoader().getResource(path).toExternalForm()
    $helpView.getEngine().load(url)
  }

  private void setupSectionView() {
    $sectionView.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        HelpItem item = $sectionView.getSelectionModel().getSelectedItem()
        changeHelp(item)
      }
    }
    for (HelpItem item : HelpItem.values()) {
      $sectionView.getItems().add(item)
    }
  }

  private void setupHelpView() {
    WebEngine engine = $helpView.getEngine()
    engine.getLoadWorker().stateProperty().addListener() { ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue ->
      if (newValue == Worker.State.SUCCEEDED) {
        Document document = $helpView.getEngine().getDocument()
        NodeList nodeList = document.getElementsByTagName("a")
        for (Integer i : 0 ..< nodeList.getLength()) {
          Node node = nodeList.item(i)
          String styleClass = ((Element)node).getAttribute("class")
          if (styleClass == "blank") {
            EventListener eventListener = { Event innerEvent ->
              HTMLAnchorElement target = (HTMLAnchorElement)innerEvent.getCurrentTarget()
              URI uri = URI.new(target.getHref())
              Desktop desktop = Desktop.getDesktop()
              desktop.browse(uri)
              innerEvent.preventDefault()
            }
            ((EventTarget)node).addEventListener("click", eventListener, false)
          }
        }
      }
    }
    File stylesheetFile = File.new(URL.new(Setting.CUSTOM_WEB_VIEW_STYLESHEET_URL).toURI())
    if (stylesheetFile.exists()) {
      engine.setUserStyleSheetLocation(Setting.CUSTOM_WEB_VIEW_STYLESHEET_URL)
    }
  }

}


@InnerClass(HelpController)
private static enum HelpItem {

  BASIC_EDIT("基本操作(編集)", "basic_edit"), BASIC_SEARCH("基本操作(検索)", "basic_search"), SLIME_EDIT("OneToMany形式の編集", "slime_edit"), SCRIPT_SEARCH("スクリプト検索", "script_search"),
  SLIME_SPECIFICATION("API(OneToMany-JSON)", "slime_specification"), PERSONAL_SPECIFICATION("API(PDIC-CSV)", "personal_specification"), TOOL("ツール", "tool"),
  SHORTCUT("ショートカットキー", "shortcut"), OTHER("その他", "other"), LICENSE("ライセンス", "license"), DICTIONARY_TYPE("各形式について", "dictionary_type")

  private static final String RESOURCE_DIRECTORY = "resource/help/"

  private String $name = ""
  private String $path = ""

  private HelpItem(String name, String path) {
    $name = name
    $path = path
  }

  public String toString() {
    return $name
  }

  public String getPath() {
    return $path + ".html"
  }

  public String getAbsolutePath() {
    return RESOURCE_DIRECTORY + $path + ".html"
  }

}