package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryExporterFactory {

  public static final List<DictionaryExporterFactory> FACTORIES = loadFactories()

  public abstract DictionarySaver create(Dictionary dictionary, ExportConfig config)

  public abstract Controller createConfigController(UtilityStage<ExportConfig> stage, Dictionary dictionary, ExportType type)

  public abstract Boolean isAvailable(Dictionary dictionary, ExportType type)

  private static List<DictionaryExporterFactory> loadFactories() {
    List<DictionaryExporterFactory> factories = ArrayList.new()
    ServiceLoader<DictionaryExporterFactory> loader = ServiceLoader.load(DictionaryExporterFactory, Thread.currentThread().getContextClassLoader())
    for (DictionaryExporterFactory factory : loader) {
      factories.add(factory)
    }
    return factories
  }

}