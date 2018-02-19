package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ExporterFactory {

  public static final List<ExporterFactory> FACTORIES = loadFactories()

  public abstract Saver create(Dictionary dictionary, ExportConfig config)

  public abstract Controller createConfigController(UtilityStage<ExportConfig> stage, Dictionary dictionary, ExportType type)

  public abstract Boolean isAvailable(Dictionary dictionary, ExportType type)

  private static List<ExporterFactory> loadFactories() {
    List<ExporterFactory> factories = ArrayList.new()
    ServiceLoader<ExporterFactory> loader = ServiceLoader.load(ExporterFactory, Thread.currentThread().getContextClassLoader())
    for (ExporterFactory factory : loader) {
      factories.add(factory)
    }
    return factories
  }

}