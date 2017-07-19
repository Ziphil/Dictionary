package ziphil.controller

import groovy.transform.CompileStatic
import java.time.LocalDateTime
import javafx.fxml.FXML
import javafx.scene.control.Label
import ziphil.Launcher
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ApplicationInformationController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/controller/application_information.fxml"
  private static final String TITLE = "ZpDICについて"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  @FXML private Label $versionLabel
  @FXML private Label $copyrightLabel

  public ApplicationInformationController(UtilityStage<? super Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

  @FXML
  private void initialize() {
    setupVersionLabel()
    setupCopyrightLabel()
  }

  private void setupVersionLabel() {
    String version = "version ${Launcher.VERSION}"
    $versionLabel.setText(version)
  }

  private void setupCopyrightLabel() {
    Int year = LocalDateTime.now().getYear()
    String copyright = "Copyright © 2016–${year}"
    $copyrightLabel.setText(copyright)
  }

}