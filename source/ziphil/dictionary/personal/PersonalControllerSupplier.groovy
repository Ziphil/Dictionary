package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphil.dictionary.ControllerSupplier
import ziphil.dictionary.SearchParameter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalControllerSupplier implements ControllerSupplier {

  private PersonalDictionary $dictionary

  public PersonalControllerSupplier(PersonalDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller getSearcherController(UtilityStage<SearchParameter> stage) {
    return null
  }

}