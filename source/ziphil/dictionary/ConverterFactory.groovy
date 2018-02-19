package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class ConverterFactory {

  public static final List<ConverterFactory> FACTORIES = loadFactories()

  public abstract Loader create(DictionaryFactory factory, Dictionary sourceDictionary)

  public abstract Boolean isAvailable(DictionaryFactory factory, Dictionary sourceDictionary)

  private static List<ConverterFactory> loadFactories() {
    List<ConverterFactory> factories = ArrayList.new()
    ServiceLoader<ConverterFactory> loader = ServiceLoader.load(ConverterFactory, Thread.currentThread().getContextClassLoader())
    for (ConverterFactory factory : loader) {
      factories.add(factory)
    }
    return factories
  }

}