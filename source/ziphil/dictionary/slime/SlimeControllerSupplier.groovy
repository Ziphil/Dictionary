package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.SlimeSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerSupplier
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

}