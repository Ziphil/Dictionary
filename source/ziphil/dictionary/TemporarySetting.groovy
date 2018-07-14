package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.NameGeneratorController
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class TemporarySetting {

  private NameGeneratorController.Config $generatorConfig = null

  public NameGeneratorController.Config getGeneratorConfig() {
    return $generatorConfig
  }

  public void setGeneratorConfig(NameGeneratorController.Config generatorConfig) {
    $generatorConfig = generatorConfig
  }

}