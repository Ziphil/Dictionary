package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class IndividualSetting {

  public abstract void save()

  public abstract List<SearchParameter> getRegisteredParameters()

  public abstract List<String> getRegisteredParameterNames()

}