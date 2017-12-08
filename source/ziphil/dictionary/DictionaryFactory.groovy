package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.custom.ExtensionFilter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryFactory {

  public static final List<DictionaryFactory> FACTORIES = lookupFactories()

  public abstract Dictionary loadDictionary(File file)

  public abstract Dictionary loadEmptyDictionary(File file)

  public abstract Dictionary convertDictionary(Dictionary oldDictionary, File file)

  public abstract Image createIcon()

  public ExtensionFilter createExtensionFilter() {
    ExtensionFilter extensionFilter = ExtensionFilter.new(getName(), getExtension())
    return extensionFilter
  }

  public abstract Boolean isConvertableFrom(Dictionary dictionary)

  public abstract Boolean isCreatable()

  public abstract String getName()

  public abstract String getExtension()

  public static Dictionary loadProperDictionary(File file) {
    if (file != null) {
      if (file.exists() && file.isFile()) {
        Dictionary dictionary = null
        String filePath = file.getPath()
        for (DictionaryFactory factory : FACTORIES) {
          if (filePath.endsWith("." + factory.getExtension())) {
            dictionary = factory.loadDictionary(file)
            dictionary.setDictionaryFactory(factory)
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

  public static Dictionary loadProperEmptyDictionary(DictionaryFactory factory, File file) {
    if (file != null) {
      Dictionary dictionary = factory.loadEmptyDictionary(file)
      dictionary.setDictionaryFactory(factory)
      return dictionary
    } else {
      return null
    }
  }

  public static Dictionary convertProperDictionary(DictionaryFactory factory, Dictionary oldDictionary, File file) {
    if (file != null) {
      Dictionary dictionary = factory.convertDictionary(oldDictionary, file)
      dictionary.setDictionaryFactory(factory)
      return dictionary
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