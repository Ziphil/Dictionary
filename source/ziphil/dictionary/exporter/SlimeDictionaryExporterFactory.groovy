package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.SlimePdfExportConfigController
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.DictionaryExporterFactory
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionaryExporterFactory extends DictionaryExporterFactory {

  public DictionarySaver create(Dictionary dictionary, ExportConfig config) {
    DictionarySaver saver = null
    if (dictionary instanceof SlimeDictionary) {
      if (config.getType() == ExportType.PDF && config instanceof SlimePdfExportConfig) {
        saver = SlimePdfDictionaryExporter.new(config)
      }
    }
    return saver
  }

  public Controller createConfigController(UtilityStage<ExportConfig> stage, Dictionary dictionary, ExportType type) {
    Controller controller = null
    if (dictionary instanceof SlimeDictionary) {
      if (type == ExportType.PDF) {
        controller = SlimePdfExportConfigController.new(stage)
      }
    }
    return controller
  }

  public Boolean isAvailable(Dictionary dictionary, ExportType type) {
    if (dictionary instanceof SlimeDictionary) {
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