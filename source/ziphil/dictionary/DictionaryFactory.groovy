package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.custom.ExtensionFilter
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryFactory {

  public static final List<DictionaryFactory> FACTORIES = loadFactories()

  protected abstract Dictionary create(File file, DictionaryLoader converter)

  public Dictionary load(File file) {
    DictionaryLoader loader = createLoader(file)
    return create(file, loader)
  }

  public Dictionary loadEmpty(File file) {
    return create(file, null)
  }

  public Dictionary convert(File file, Dictionary sourceDictionary) {
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
    return create(file, converter)
  }

  public void save(Dictionary dictionary) {
    DictionarySaver saver = createSaver()
    dictionary.save(saver)
  }

  public void saveBackup(Dictionary dictionary) {
    DictionarySaver saver = createSaver()
    saver.setPath(dictionary.getPath().replaceAll(/(?=\.\w+$)/, "_backup"))
    dictionary.save(saver)
  }

  public void export(Dictionary dictionary, ExportConfig config) {
    DictionarySaver exporter = null
    for (DictionaryExporterFactory factory : DictionaryExporterFactory.FACTORIES) {
      if (factory.isAvailable(dictionary, config.getType())) {
        exporter = factory.create(dictionary, config)
        exporter.setPath(config.getPath())
        break
      }
    }
    dictionary.save(exporter)
  }

  protected abstract DictionaryLoader createLoader(File file)

  protected abstract DictionarySaver createSaver()

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

  public Boolean isExportableTo(Dictionary dictionary, ExportType type) {
    for (DictionaryExporterFactory factory : DictionaryExporterFactory.FACTORIES) {
      if (factory.isAvailable(dictionary, type)) {
        return true
      }
    }
  }

  public abstract Boolean isCreatable()

  public abstract String getName()

  public abstract String getExtension()

  public abstract Class<? extends Dictionary> getDictionaryClass()

  public static Dictionary loadProper(File file) {
    if (file != null) {
      if (file.exists() && file.isFile()) {
        Dictionary dictionary = null
        String filePath = file.getPath()
        for (DictionaryFactory factory : FACTORIES) {
          if (filePath.endsWith("." + factory.getExtension())) {
            dictionary = factory.load(file)
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

  public static Dictionary loadProperEmpty(DictionaryFactory factory, File file) {
    if (file != null) {
      Dictionary dictionary = factory.loadEmpty(file)
      dictionary.setDictionaryFactory(factory)
      return dictionary
    } else {
      return null
    }
  }

  public static Dictionary convertProper(DictionaryFactory factory, Dictionary sourceDictionary, File file) {
    if (file != null) {
      Dictionary dictionary = factory.convert(file, sourceDictionary)
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