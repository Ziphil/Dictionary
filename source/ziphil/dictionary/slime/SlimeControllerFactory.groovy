package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.SlimeIndividualSettingController
import ziphil.controller.SlimeSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerFactory
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeControllerFactory implements ControllerFactory {

  private SlimeDictionary $dictionary

  public SlimeControllerFactory(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller createExporterController(UtilityStage<ExportConfig> stage) {
    return null
  }

  public Controller createSearcherController(UtilityStage<SearchParameter> stage) {
    SlimeSearcherController controller = SlimeSearcherController.new(stage)
    controller.prepare($dictionary)
    return controller
  }

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting) {
    SlimeIndividualSettingController controller = SlimeIndividualSettingController.new(stage)
    controller.prepare($dictionary, (SlimeIndividualSetting)individualSetting)
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