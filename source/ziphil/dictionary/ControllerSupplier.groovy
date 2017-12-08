package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage


@CompileStatic
public interface ControllerSupplier {

  public Controller getSearcherController(UtilityStage<SearchParameter> stage)
  
}