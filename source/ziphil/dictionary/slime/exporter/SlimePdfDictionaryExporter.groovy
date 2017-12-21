package ziphil.dictionary.slime.exporter

import groovy.transform.CompileStatic
import ziphil.dictionary.ExportConfig
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfDictionaryExporter extends SlimeTemporaryXmlDictionaryExporter {

  private ExportConfig $config

  public SlimePdfDictionaryExporter(ExportConfig config) {
    super()
    $config = config
  }

  protected BooleanClass save() {
    String temporaryPath = $path.replaceAll(/\.\w+$/, "_temp.xml")
    saveTemporary(temporaryPath)
  }

}