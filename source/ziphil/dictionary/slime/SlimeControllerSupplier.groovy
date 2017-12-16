package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.SlimeIndividualSettingController
import ziphil.controller.SlimeSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerSupplier
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeControllerSupplier implements ControllerSupplier {

  private SlimeDictionary $dictionary

  public SlimeControllerSupplier(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller getSearcherController(UtilityStage<SearchParameter> stage) {
    SlimeSearcherController controller = SlimeSearcherController.new(stage)
    controller.prepare($dictionary)
    return controller
  }

  public Controller getIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting) {
    SlimeIndividualSettingController controller = SlimeIndividualSettingController.new(stage)
    controller.prepare($dictionary, (SlimeIndividualSetting)individualSetting)
    return controller
  }

  public Boolean isSearcherControllerSupported() {
    return true
  }

  public Boolean isIndividualSettingControllerSupported() {
    return true
  }

}