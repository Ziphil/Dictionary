package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryFactory {

  public static final List<DictionaryFactory> FACTORIES = lookupFactories()

  public abstract Dictionary loadDictionary(File file)

  public abstract Dictionary loadEmptyDictionary(File file)

  public abstract Dictionary convertDictionary(Dictionary oldDictionary, File file)

  public abstract String getExtension()

  public static Dictionary loadProperDictionary(File file) {
    if (file != null) {
      if (file.exists() && file.isFile()) {
        Dictionary dictionary = null
        String filePath = file.getPath()
        for (DictionaryFactory factory : FACTORIES) {
          if (filePath.endsWith("." + factory.getExtension())) {
            dictionary = factory.loadDictionary(file)
            break
          }
        }
        return dictionary
      } else {
        return null
      }
    } else {
      return null
    }
  }

  private static List<DictionaryFactory> lookupFactories() {
    List<DictionaryFactory> factories = ArrayList.new()
    ServiceLoader<DictionaryFactory> loader = ServiceLoader.load(DictionaryFactory, Thread.currentThread().getContextClassLoader())
    println("**")
    for (DictionaryFactory factory : loader) {
      print("found: ")
      println(factory.getClass().getName())
      factories.add(factory)
    }
    return factories
  }

}