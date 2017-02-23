package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.web.WebView
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HelpController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/help.fxml"
  private static final String BASIC_EDIT_HTML_PATH = "resource/help/basic_edit.html"
  private static final String BASIC_SEARCH_HTML_PATH = "resource/help/basic_search.html"
  private static final String SLIME_EDIT_HTML_PATH = "resource/help/slime_edit.html"
  private static final String SHORTCUT_HTML_PATH = "resource/help/shortcut.html"
  private static final String OTHER_HTML_PATH = "resource/help/other.html"
  private static final String LICENSE_HTML_PATH = "resource/help/license.html"
  private static final String DICTIONARY_TYPE_HTML_PATH = "resource/help/dictionary_type.html"
  private static final String TITLE = "ヘルプ"
  private static final Double DEFAULT_WIDTH = 640
  private static final Double DEFAULT_HEIGHT = 480

  @FXML private ListView<String> $sectionsView
  @FXML private WebView $helpView

  public HelpController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT)
  }

  @FXML
  private void initialize() {
    setupSectionsView()
  }

  private void changeHelp(String section) {
    String url = ""
    if (section == "基本操作(編集)") {
      url = getClass().getClassLoader().getResource(BASIC_EDIT_HTML_PATH).toExternalForm()
    } else if (section == "基本操作(検索)") {
      url = getClass().getClassLoader().getResource(BASIC_SEARCH_HTML_PATH).toExternalForm()
    } else if (section == "OneToMany形式の編集") {
      url = getClass().getClassLoader().getResource(SLIME_EDIT_HTML_PATH).toExternalForm()
    } else if (section == "ショートカットキー") {
      url = getClass().getClassLoader().getResource(SHORTCUT_HTML_PATH).toExternalForm()
    } else if (section == "その他") {
      url = getClass().getClassLoader().getResource(OTHER_HTML_PATH).toExternalForm()
    } else if (section == "ライセンス") {
      url = getClass().getClassLoader().getResource(LICENSE_HTML_PATH).toExternalForm()
    } else if (section == "各形式について") {
      url = getClass().getClassLoader().getResource(DICTIONARY_TYPE_HTML_PATH).toExternalForm()
    }
    $helpView.getEngine().load(url)
  }

  private void setupSectionsView() {
    $sectionsView.addEventHandler(MouseEvent.MOUSE_CLICKED) { MouseEvent event ->
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        String section = $sectionsView.getSelectionModel().getSelectedItems()[0]
        changeHelp(section)
      }
    }
  }

}