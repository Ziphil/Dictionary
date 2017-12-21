package ziphil.dictionary.slime.exporter

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfDictionaryExporter extends DictionarySaver<SlimeDictionary> {

  private ExportConfig $config

  public SlimePdfDictionaryExporter(ExportConfig config) {
    super()
    $config = config
  }

  protected BooleanClass save() {
    println("Hello")
  }

}