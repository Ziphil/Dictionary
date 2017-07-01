package ziphil.dictionary.slime

import groovy.transform.CompileStatic


@CompileStatic
public interface SlimeRelationRequest {

  public SlimeWord getWord()

  public SlimeRelation getRelation()

}