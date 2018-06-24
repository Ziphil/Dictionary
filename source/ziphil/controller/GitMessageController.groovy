package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.Setting
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class GitMessageController extends Controller<String> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/git_message.fxml"
  private static final String TITLE = "コミットメッセージ編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $messageControl

  public GitMessageController(UtilityStage<? super String> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupMessageControl()
  }

  @FXML
  protected void commit() {
    String message = $messageControl.getText()
    $stage.commit(message)
  }

  private void setupMessageControl() {
    String defaultMessage = Setting.getInstance().getDefaultGitMessage()
    $messageControl.setText(defaultMessage)
  }

}