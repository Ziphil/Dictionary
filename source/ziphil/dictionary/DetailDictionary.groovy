package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic 
public interface DetailDictionary<P extends DetailSearchParameter> {

  public void searchDetail(P parameter)

}