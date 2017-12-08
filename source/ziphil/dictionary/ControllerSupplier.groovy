package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface ControllerSupplier {

  public Controller getSearcherController(UtilityStage<SearchParameter> stage)

  public Controller getIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting)
 
  public Boolean isSearcherControllerSupported()

  public Boolean isIndividualSettingControllerSupported()

}