package ziphil.controller

import groovy.transform.CompileStatic
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import javafx.fxml.FXML
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.concurrent.Worker
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
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
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HelpController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/help.fxml"
  private static final String TITLE = "ヘルプ"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(720)
  private static final Double DEFAULT_HEIGHT = Measurement.rpx(480)

  @FXML private TreeView<HelpSection> $sectionView
  @FXML private WebView $helpView

  public HelpController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupSectionView()
    setupHelpView()
  }

  private void changeHelp(HelpSection section) {
    String path = section.getAbsolutePath()
    if (path != null) {
      String url = getClass().getClassLoader().getResource(path).toExternalForm()
      $helpView.getEngine().load(url)
    }
  }

  private void setupSectionView() {
    $sectionView.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        TreeItem<HelpSection> item = $sectionView.getSelectionModel().getSelectedItem()
        if (item != null) {
          HelpSection section = item.getValue()
          changeHelp(section)
        }
      }
    }
    $sectionView.setRoot(HelpItem.createRoot())
  }

  private void setupHelpView() {
    WebEngine engine = $helpView.getEngine()
    engine.getLoadWorker().stateProperty().addListener() { ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue ->
      if (newValue == Worker.State.SUCCEEDED) {
        Document document = $helpView.getEngine().getDocument()
        NodeList nodeList = document.getElementsByTagName("a")
        for (Int i = 0 ; i < nodeList.getLength() ; i ++) {
          Node node = nodeList.item(i)
          String styleClass = ((Element)node).getAttribute("class")
          if (styleClass == "blank") {
            EventListener eventListener = { Event innerEvent ->
              HTMLAnchorElement target = (HTMLAnchorElement)innerEvent.getCurrentTarget()
              URI uri = URI.new(target.getHref())
              if (Desktop.isDesktopSupported() && !GraphicsEnvironment.isHeadless()) {
                Desktop desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                  try {
                    desktop.browse(uri)
                  } catch (Exception exception) {
                  }
                }
              }
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
@CompileStatic @Ziphilify
private static enum HelpSection {

  BASIC("基本操作", null),
  BASIC_EDIT("編集", "basic_edit", BASIC),
  BASIC_SEARCH("検索", "basic_search", BASIC),
  EDIT("編集方法詳細", null),
  SLIME_EDIT("OneToMany形式", "slime_edit", EDIT),
  BADGE("マーカー", "badge"),
  SCRIPT_SEARCH("スクリプト検索", "script_search"),
  SENTENCE_SEARCH("文一括検索", "sentence_search"),
  WORD_GENERATION("単語の自動生成", "word_generation"),
  EXPORT("別形式へのエクスポート", "export"),
  SETTING("環境設定", "setting"),
  INDIVIDUAL_SETTING("辞書の個別設定", null),
  SLIME_INDIVIDUAL_SETTING("OneToMany形式", "slime_individual_setting", INDIVIDUAL_SETTING),
  SPECIFICATION("単語API", null),
  SLIME_SPECIFICATION("OneToMany形式", "slime_specification", SPECIFICATION),
  PERSONAL_SPECIFICATION("PDIC形式", "personal_specification", SPECIFICATION),
  GIT("Git", "git"),
  PLUGIN("プラグイン", "plugin"),
  TOOL("ツール", null),
  HAH_COMPRESSION("hah圧縮", "hah_compression", TOOL),
  AKRANTIAIN("akrantiain", "akrantiain", TOOL),
  ZATLIN("Zatlin", "zatlin", TOOL),
  EASY_NAME_GENERATION("簡易単語生成", "easy_name_generation", TOOL),
  SHORTCUT("ショートカットキー", "shortcut"),
  OTHER("その他", "other"),
  LICENSE("ライセンス", "license"),
  DICTIONARY_TYPE("各形式について", "dictionary_type")

  private static final String RESOURCE_DIRECTORY = "resource/help/"

  private String $name = null
  private String $path = null
  private HelpSection $parent = null

  private HelpSection(String name, String path) {
    $name = name
    $path = path
  }

  private HelpSection(String name, String path, HelpSection parent) {
    $name = name
    $path = path
    $parent = parent
  }

  public String toString() {
    return $name
  }

  public String getPath() {
    return ($path != null) ? $path + ".html" : null
  }

  public String getAbsolutePath() {
    return ($path != null) ? RESOURCE_DIRECTORY + $path + ".html" : null
  }

  public HelpSection getParent() {
    return $parent
  }

}


@InnerClass(HelpController)
@CompileStatic @Ziphilify
private static class HelpItem extends TreeItem<HelpSection> {

  private Boolean $leaf = true
  private Boolean $leafDetermined = false
  private Boolean $childrenCreated = false

  private HelpItem(HelpSection section) {
    super(section)
    setup()
  }

  public static TreeItem<HelpSection> createRoot() {
    return HelpItem.new(null)
  }

  public Boolean isLeaf() {
    if (!$leafDetermined) {
      $leafDetermined = true
      HelpSection section = getValue()
      for (HelpSection eachSection : HelpSection.values()) {
        if (eachSection.getParent() == section) {
          $leaf = false
          break
        }
      }
    }
    return $leaf
  }

  public ObservableList<TreeItem<HelpSection>> getChildren() {
    if (!$childrenCreated) {
      $childrenCreated = true
      HelpSection section = getValue()
      List<TreeItem<HelpSection>> children = ArrayList.new()
      for (HelpSection eachSection : HelpSection.values()) {
        if ((section != null && eachSection.getParent() == section) || (section == null && eachSection.getParent() == null)) {
          children.add(HelpItem.new(eachSection))
        }
      }
      super.getChildren().setAll(children)
    }
    return super.getChildren()
  }

  private void setup() {
    setExpanded(true)
    expandedProperty().addListener() { ObservableValue<? extends BooleanClass> observableValue, BooleanClass oldValue, BooleanClass newValue ->
      if (!newValue) {
        setExpanded(true)
      }
    }
  }

}