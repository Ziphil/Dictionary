package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinRoot {

  private List<ZatlinDefinition> $definitions = ArrayList.new()
  private ZatlinGeneratable $mainGeneratable = null

  public String generate() {
    return $mainGeneratable.generate(this)
  }

  public ZatlinDefinition findDefinitionOf(String identifierName) {
    for (ZatlinDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return definition
      }
    }
    return null
  }

  public ZatlinGeneratable findContentOf(String identifierName) {
    for (ZatlinDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return definition.getContent()
      }
    }
    return null
  }

  public ZatlinToken findUnknownIdentifier() {
    for (ZatlinDefinition definition : $definitions) {
      ZatlinToken unknownIdentifier = definition.findUnknownIdentifier(this)
      if (unknownIdentifier != null) {
        return unknownIdentifier
      }
    }
    ZatlinToken unknownIdentifier = $mainGeneratable.findUnknownIdentifier(this)
    if (unknownIdentifier != null) {
      return unknownIdentifier
    }
    return null
  }

  public ZatlinToken findCircularIdentifier() {
    for (ZatlinDefinition definition : $definitions) {
      ZatlinToken circularIdentifier = definition.findCircularIdentifier(this)
      if (circularIdentifier != null) {
        return circularIdentifier
      }
    }
    return null
  }

  public Boolean containsDefinitionOf(String identifierName) {
    for (ZatlinDefinition definition : $definitions) {
      if (definition.getIdentifier().getText() == identifierName) {
        return true
      }
    }
    return false
  }

  public Boolean hasMainGeneratable() {
    return $mainGeneratable != null
  }

  public List<ZatlinDefinition> getDefinitions() {
    return $definitions
  }

  public void setDefinitions(List<ZatlinDefinition> definitions) {
    $definitions = definitions
  }

  public ZatlinGeneratable getMainGeneratable() {
    return $mainGeneratable
  }

  public void setMainGeneratable(ZatlinGeneratable mainGeneratable) {
    $mainGeneratable = mainGeneratable
  }

}