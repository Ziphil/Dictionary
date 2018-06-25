package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphil.module.Setting
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class GitPushConfigController extends Controller<PushCommand> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/git_push_config.fxml"
  private static final String TITLE = "コマンド引数編集"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $remoteControl
  @FXML private CheckBox $omitsRemoteControl
  @FXML private TextField $refSpecControl
  @FXML private CheckBox $omitsRefSpecControl
  @FXML private TextField $usernameControl
  @FXML private TextField $passwordControl
  @FXML private CheckBox $omitsCredentialsControl
  private Git $git

  public GitPushConfigController(UtilityStage<? super PushCommand> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupControls()
  }

  public void prepare(Git git) {
    $git = git
    try {
      List<RemoteConfig> remoteConfigs = git.remoteList().call()
      String firstRemoteName = remoteConfigs[0].getName()
      $remoteControl.setText(firstRemoteName)
    } catch (Exception exception) {
    }
    try {
      List<Ref> refs = git.branchList().setContains("HEAD").call()
      String currentBranchName = refs[0].getName()
      $refSpecControl.setText(currentBranchName)
    } catch (Exception exception) {
    }
  }

  @FXML
  protected void commit() {
    PushCommand command = $git.push()
    command.setTimeout(5)
    if (!$omitsRemoteControl.isSelected()) {
      command.setRemote($remoteControl.getText())
    }
    if (!$omitsRefSpecControl.isSelected()) {
      RefSpec refSpec = RefSpec.new($refSpecControl.getText())
      command.setRefSpecs(refSpec)
    }
    if (!$omitsRemoteControl.isSelected()) {
      UsernamePasswordCredentialsProvider credentialsProvider = UsernamePasswordCredentialsProvider.new($usernameControl.getText(), $passwordControl.getText())
      command.setCredentialsProvider(credentialsProvider)
    }
    $stage.commit(command)
  }

  private void setupControls() {
    $remoteControl.disableProperty().bind($omitsRemoteControl.selectedProperty())
    $refSpecControl.disableProperty().bind($omitsRefSpecControl.selectedProperty())
    $usernameControl.disableProperty().bind($omitsCredentialsControl.selectedProperty())
    $passwordControl.disableProperty().bind($omitsCredentialsControl.selectedProperty())
  }

}