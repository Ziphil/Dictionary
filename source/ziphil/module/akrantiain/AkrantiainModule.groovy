package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainModule {

  private static final String PUNCTUATION_IDENTIIER_NAME = "PUNCTUATION"

  private AkrantiainModuleName $name = AkrantiainModuleName.new()
  private Set<AkrantiainEnvironment> $environments = Collections.synchronizedSet(EnumSet.noneOf(AkrantiainEnvironment))
  private List<AkrantiainDefinition> $definitions = Collections.synchronizedList(ArrayList.new())
  private List<AkrantiainRule> $rules = Collections.synchronizedList(ArrayList.new())
  private List<AkrantiainModuleName> $moduleChain = Collections.synchronizedList(ArrayList.new())

  public String convert(String input, AkrantiainRoot root) {
    String currentOutput = input
    currentOutput = convertByRule(currentOutput, root)
    currentOutput = convertByModuleChain(currentOutput, root)
    return currentOutput
  }

  private String convertByRule(String input, AkrantiainRoot root) {
    if (!$rules.isEmpty()) {
      AkrantiainElementGroup currentGroup = AkrantiainElementGroup.create(input)
      for (AkrantiainRule rule : $rules) {
        currentGroup = rule.apply(currentGroup, this)
      }
      List<AkrantiainElement> invalidElements = currentGroup.invalidElements(this)
      if (invalidElements.isEmpty()) {
        return currentGroup.createOutput()
      } else {
        throw AkrantiainException.new("No rules that can handle some characters", invalidElements)
      }
    } else {
      return input
    }
  }

  private String convertByModuleChain(String input, AkrantiainRoot root) {
    String currentOutput = input
    for (AkrantiainModuleName moduleName : $moduleChain) {
      AkrantiainModule module = root.findModuleOf(moduleName)
      if (module != null) {
        currentOutput = module.convert(currentOutput, root)
      } else {
        throw AkrantiainException.new("This cannot happen")
      }
    }
    return currentOutput
  }

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
      if (definition.getIdentifier().getText() == PUNCTUATION_IDENTIIER_NAME) {
        return definition.getContent()
      }
    }
    return AkrantiainDisjunction.EMPTY_DISJUNCTION
  }

  public Boolean containsEnvironment(AkrantiainEnvironment environment) {
    return $environments.contains(environment)
  } 

  public Boolean containsDefinitionOf(AkrantiainToken identifier) {
    for (AkrantiainDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifier.getText()) {
        return true
      }
    }
    return false
  }

  public String toString() {
    StringBuilder string = StringBuilder.new()
    string.append("% ")
    string.append($name)
    string.append("{")
    string.append("\nenvironments:")
    for (Integer i : 0 ..< $environments.size()) {
      string.append("\n  ")
      string.append($environments[i])
    }
    string.append("\ndefinitions:")
    for (Integer i : 0 ..< $definitions.size()) {
      string.append("\n  ")
      string.append($definitions[i])
    }
    string.append("\nrules:")
    for (Integer i : 0 ..< $rules.size()) {
      string.append("\n  ")
      string.append($rules[i])
    }
    string.append("\nmodule chain:")
    for (Integer i : 0 ..< $moduleChain.size()) {
      string.append("\n  ")
      string.append($moduleChain[i])
    }
    string.append("\n}")
    return string.toString()
  }

  public AkrantiainModuleName getName() {
    return $name
  }

  public void setName(AkrantiainModuleName name) {
    $name = name
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

  public List<AkrantiainModuleName> getModuleChain() {
    return $moduleChain
  }

  public void setModuleChain(List<AkrantiainModuleName> moduleChain) {
    $moduleChain = moduleChain
  }

}