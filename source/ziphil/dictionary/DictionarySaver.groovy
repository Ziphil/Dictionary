package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionarySaver<D extends Dictionary> extends Task<Boolean> {

  protected D $dictionary
  protected String $path

  public DictionarySaver(D dictionary, String path) {
    $path = path
    $dictionary = dictionary
  }

  protected abstract Boolean save()

  protected Boolean call() {
    if ($path != null) {
      Boolean result = save()
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