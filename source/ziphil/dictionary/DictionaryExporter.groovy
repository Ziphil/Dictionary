package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryExporter<D extends Dictionary, C extends ExportConfig> extends Task<BooleanClass> {

  protected D $dictionary
  protected C $config

  public DictionaryExporter(D dictionary, C config) {
    $dictionary = dictionary
    $config = config
  }

  protected abstract BooleanClass export()

  protected BooleanClass call() {
    if ($config.getPath() != null) {
      BooleanClass result = export()
      return result
    } else {
      return false
    }
  }

}