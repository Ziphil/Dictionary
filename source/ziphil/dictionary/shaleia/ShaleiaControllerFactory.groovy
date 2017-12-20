package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.ShaleiaIndividualSettingController
import ziphil.controller.ShaleiaSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerFactory
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaControllerFactory implements ControllerFactory {

  private ShaleiaDictionary $dictionary

  public ShaleiaControllerFactory(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller createExporterController(UtilityStage<ExportConfig> stage) {
    return null
  }

  public Controller createSearcherController(UtilityStage<SearchParameter> stage) {
    ShaleiaSearcherController controller = ShaleiaSearcherController.new(stage)
    return controller
  }

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting) {
    ShaleiaIndividualSettingController controller = ShaleiaIndividualSettingController.new(stage)
    controller.prepare($dictionary)
    return controller
  }

  public Boolean isExporterSupported(ExportType type) {
    return false
  }

  public Boolean isSearcherSupported() {
    return true
  }

  public Boolean isIndividualSettingSupported() {
    return true
  }

}