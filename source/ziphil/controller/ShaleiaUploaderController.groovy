package ziphil.controller

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import ziphil.custom.Measurement
import ziphil.custom.UtilityStage
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaUploaderController extends Controller<ShaleiaUploaderController.Result> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/shaleia_uploader.fxml"
  private static final String TITLE = "辞典アップロード"
  private static final Double DEFAULT_WIDTH = Measurement.rpx(480)
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private TextField $urlTextControl
  @FXML private PasswordField $passwordControl

  public ShaleiaUploaderController(UtilityStage<? super ShaleiaUploaderController.Result> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  public void prepare(String urlText) {
    $urlTextControl.setText(urlText)
    $passwordControl.requestFocus()
  }

  @FXML
  protected void commit() {
    String urlText = $urlTextControl.getText()
    String password = $passwordControl.getText()
    Result result = Result.new(urlText, password)
    $stage.commit(result)
  }

}


@InnerClass(ShaleiaUploaderController)
@CompileStatic @Ziphilify
public static class Result {

  private String $urlText
  private String $password

  public Result(String urlText, String password) {
    $urlText = urlText
    $password = password
  }

  public String getUrlText() {
    return $urlText
  }

  public String getPassword() {
    return $password
  }

}