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

  protected abstract Boolean call()

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

}