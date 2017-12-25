package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryExporterFactory
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionaryExporterFactory extends DictionaryExporterFactory {

  public DictionarySaver create(Dictionary dictionary, ExportConfig config) {
    DictionarySaver saver = null
    if (dictionary instanceof ShaleiaDictionary) {
      if (config.getType() == ExportType.PDF) {
        saver = ShaleiaPdfDictionaryExporter.new(config)
      }
    }
    return saver
  }

  public Boolean isAvailable(Dictionary dictionary, ExportType type) {
    if (dictionary instanceof ShaleiaDictionary) {
      if (type == ExportType.PDF) {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }

}