package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.TextField
import org.eclipse.jgit.api.CommitCommand
import org.eclipse.jgit.api.Git
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.Setting
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class GitCommitConfigController extends Controller<CommitCommand> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/git_commit_config.fxml"
  private static final String TITLE = "コミットメッセージ編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $messageControl
  private Git $git

  public GitCommitConfigController(UtilityStage<? super CommitCommand> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupMessageControl()
  }

  public void prepare(Git git) {
    $git = git
  }

  @FXML
  protected void commit() {
    CommitCommand command = $git.commit()
    command.setMessage($messageControl.getText())
    $stage.commit(command)
  }

  private void setupMessageControl() {
    String defaultMessage = Setting.getInstance().getDefaultGitMessage()
    $messageControl.setText(defaultMessage)
  }

}