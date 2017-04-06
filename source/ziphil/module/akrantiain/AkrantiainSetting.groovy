package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainSetting {

  private Set<AkrantiainEnvironment> $environments = EnumSet.noneOf(AkrantiainEnvironment)
  private List<AkrantiainDefinition> $definitions = Collections.synchronizedList(ArrayList.new())
  private List<AkrantiainRule> $rules = Collections.synchronizedList(ArrayList.new())

  public AkrantiainMatchable findContentOf(String identifierName) {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return definition.getContent()
      }
    }
    return null
  }

  public AkrantiainMatchable findPunctuationContent() {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == Akrantiain.PUNCTUATION_IDENTIIER_NAME) {
        return definition.getContent()
      }
    }
    return AkrantiainDisjunction.EMPTY_DISJUNCTION
  }

  public Boolean containsEnvironment(AkrantiainEnvironment environment) {
    return $environments.contains(environment)
  } 

  public Boolean containsIdentifier(AkrantiainToken identifier) {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifier.getText()) {
        return true
      }
    }
    return false
  }

  public Set<AkrantiainEnvironment> getEnvironments() {
    return $environments
  }

  public void setEnvironments(Set<AkrantiainEnvironment> environments) {
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