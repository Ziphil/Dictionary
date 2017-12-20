package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerFactory
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.IndividualSetting
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalControllerFactory implements ControllerFactory {

  private PersonalDictionary $dictionary

  public PersonalControllerFactory(PersonalDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller createExporterController(UtilityStage<ExportConfig> stage) {
    return null
  }

  public Controller createSearcherController(UtilityStage<SearchParameter> stage) {
    return null
  }

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, IndividualSetting individualSetting) {
    return null
  }

  public Boolean isExporterSupported(ExportType type) {
    return false
  }
 
  public Boolean isSearcherSupported() {
    return false
  }

  public Boolean isIndividualSettingSupported() {
    return false
  }

}