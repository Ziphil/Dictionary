package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.ShaleiaSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerSupplier
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

}