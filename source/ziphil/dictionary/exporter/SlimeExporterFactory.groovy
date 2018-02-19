package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.SlimePdfExportConfigController
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.ExporterFactory
import ziphil.dictionary.Saver
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.ExportType
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeExporterFactory extends ExporterFactory {

  public Saver create(Dictionary dictionary, ExportConfig config) {
    Saver saver = null
    if (dictionary instanceof SlimeDictionary) {
      if (config.getType() == ExportType.PDF && config instanceof SlimePdfExportConfig) {
        saver = SlimePdfExporter.new(config)
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