package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSetting {

  private EnumSet<AkrantiainEnvironment> $environments = EnumSet.noneOf(AkrantiainEnvironment)
  private List<AkrantiainDefinition> $definitions = ArrayList.new()
  private List<AkrantiainRule> $rules = ArrayList.new()

  public AkrantiainDisjunctionGroup findGroupOf(String identifierName) {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return definition.getGroup()
      }
    }
    throw AkrantiainException.new("No such identifier")
  }

  public AkrantiainDisjunctionGroup findPunctuationGroup() {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == "PUNCTUATION") {
        return definition.getGroup()
      }
    }
    return AkrantiainDisjunctionGroup.EMPTY_GROUP
  }

  public Boolean containsEnvironment(AkrantiainEnvironment environment) {
    return $environments.contains(environment)
  } 

  public EnumSet<AkrantiainEnvironment> getEnvironments() {
    return $environments
  }

  public void setEnvironments(EnumSet<AkrantiainEnvironment> environments) {
    $environments = environments
  }

  public List<AkrantiainDefinition> getDefinitions() {
    return $definitions
  }

  public void setDefinitions(List<AkrantiainDefinition> definitions) {
    $definitions = definitions
  }

  public List<AkrantiainRule> getRules() {
    return $rules
  }

  public void setRules(List<AkrantiainRule> rules) {
    $rules = rules
  }

}