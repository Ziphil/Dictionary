package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryConverterFactory {

  public static final List<DictionaryConverterFactory> FACTORIES = loadFactories()

  public abstract DictionaryConverter create(DictionaryFactory factory, Dictionary sourceDictionary)

  public abstract Boolean isAvailable(DictionaryFactory factory, Dictionary sourceDictionary)

  private static List<DictionaryConverterFactory> loadFactories() {
    List<DictionaryConverterFactory> factories = ArrayList.new()
    ServiceLoader<DictionaryConverterFactory> loader = ServiceLoader.load(DictionaryConverterFactory, Thread.currentThread().getContextClassLoader())
    for (DictionaryConverterFactory factory : loader) {
      factories.add(factory)
    }
    return factories
  }

}