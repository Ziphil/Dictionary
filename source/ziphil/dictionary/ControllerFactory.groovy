package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface ControllerFactory {

  public Controller createSearcherController(UtilityStage<SearchParameter> stage)

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting)
 
  public Boolean isSearcherSupported()

  public Boolean isIndividualSettingSupported()

}