package ziphil.controller

import groovy.transform.CompileStatic
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class UpdateInformationController extends Controller<Void> {

  private static final String RESOURCE_PATH = "resource/fxml/update_information.fxml"
  private static final String TITLE = "更新情報"
  private static final Double DEFAULT_WIDTH = -1
  private static final Double DEFAULT_HEIGHT = -1

  public UpdateInformationController(UtilityStage<Void> stage) {
    super(stage)
    loadResource(RESOURCE_PATH, TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT, false)
  }

}