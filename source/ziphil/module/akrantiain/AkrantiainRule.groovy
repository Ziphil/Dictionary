package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainRule {

  private List<AkrantiainStringGroup> $selections
  private AkrantiainStringGroup $leftCondition
  private AkrantiainStringGroup $rightCondition

  public AkrantiainRule(List<AkrantiainStringGroup> selections, AkrantiainStringGroup leftCondition, AkrantiainStringGroup rightCondition) {
    $selections = selections
    $leftCondition = leftCondition
    $rightCondition = rightCondition
  }

}