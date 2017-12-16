package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.ShaleiaIndividualSettingController
import ziphil.controller.ShaleiaSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerSupplier
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaControllerSupplier implements ControllerSupplier {

  private ShaleiaDictionary $dictionary

  public ShaleiaControllerSupplier(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller getSearcherController(UtilityStage<SearchParameter> stage) {
    ShaleiaSearcherController controller = ShaleiaSearcherController.new(stage)
    return controller
  }

  public Controller getIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting) {
    ShaleiaIndividualSettingController controller = ShaleiaIndividualSettingController.new(stage)
    controller.prepare($dictionary)
    return controller
  }

  public Boolean isSearcherControllerSupported() {
    return true
  }

  public Boolean isIndividualSettingControllerSupported() {
    return true
  }

}