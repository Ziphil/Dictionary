package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@JsonIgnoreProperties(ignoreUnknown=true)
@CompileStatic @Ziphilify
public class SimpleIndividualSetting extends IndividualSetting {

  public static SimpleIndividualSetting create(Dictionary dictionary) {
    return IndividualSetting.create(dictionary, SimpleIndividualSetting)
  }

  public List<SearchParameter> getRegisteredParameters() {
    return null
  }

  public List<String> getRegisteredParameterNames() {
    return null
  }

}