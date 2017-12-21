package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.custom.ExtensionFilter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryFactory {

  public static final List<DictionaryFactory> FACTORIES = loadFactories()

  public abstract Dictionary loadDictionary(File file)

  public abstract Dictionary loadEmptyDictionary(File file)

  public abstract Dictionary convertDictionary(File file, DictionaryLoader converter)

  public Dictionary convertDictionary(File file, Dictionary sourceDictionary) {
    DictionaryLoader converter = null
    if (sourceDictionary.getDictionaryFactory() != this) {
      for (DictionaryConverterFactory factory : DictionaryConverterFactory.FACTORIES) {
        if (factory.isAvailable(this, sourceDictionary)) {
          converter = factory.create(this, sourceDictionary)
          break
        }
      }
    } else {
      converter = IdentityDictionaryConverter.new(sourceDictionary)
    }
    if (converter == null) {
      converter = EmptyDictionaryConverter.new(sourceDictionary)
    }
    return convertDictionary(file, converter)
  }

  public abstract Image createIcon()

  public ExtensionFilter createExtensionFilter() {
    ExtensionFilter extensionFilter = ExtensionFilter.new(getName(), getExtension())
    return extensionFilter
  }

  public Boolean isConvertableFrom(Dictionary sourceDictionary) {
    if (sourceDictionary.getDictionaryFactory() != this) {
      for (DictionaryConverterFactory factory : DictionaryConverterFactory.FACTORIES) {
        if (factory.isAvailable(this, sourceDictionary)) {
          return true
        }
      }
      return false
    } else {
      return true
    }
  }

  public abstract Boolean isCreatable()

  public abstract String getName()

  public abstract String getExtension()

  public abstract Class<? extends Dictionary> getDictionaryClass()

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

  public static Dictionary convertProperDictionary(DictionaryFactory factory, Dictionary sourceDictionary, File file) {
    if (file != null) {
      Dictionary dictionary = factory.convertDictionary(file, sourceDictionary)
      dictionary.setDictionaryFactory(factory)
      return dictionary
    } else {
      return null
    }
  }

  private static List<DictionaryFactory> loadFactories() {
    List<DictionaryFactory> factories = ArrayList.new()
    ServiceLoader<DictionaryFactory> loader = ServiceLoader.load(DictionaryFactory, Thread.currentThread().getContextClassLoader())
    for (DictionaryFactory factory : loader) {
      factories.add(factory)
    }
    return factories
  }

}