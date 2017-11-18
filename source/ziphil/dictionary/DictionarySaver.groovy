package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionarySaver<D extends Dictionary> extends Task<BooleanClass> {

  protected D $dictionary
  protected String $path

  public DictionarySaver(D dictionary, String path) {
    $dictionary = dictionary
    $path = path
  }

  protected abstract BooleanClass save()

  protected BooleanClass call() {
    if ($path != null) {
      BooleanClass result = save()
      return result
    } else {
      return false
    }
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

}