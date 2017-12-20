package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface ControllerFactory {

  public Controller createExporterController(UtilityStage<ExportConfig> stage)

  public Controller createSearcherController(UtilityStage<SearchParameter> stage)

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting)

  public Boolean isExporterSupported()
 
  public Boolean isSearcherSupported()

  public Boolean isIndividualSettingSupported()

}