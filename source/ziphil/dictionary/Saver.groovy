package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class Saver<D extends Dictionary> extends Task<BooleanClass> {

  protected D $dictionary
  protected String $path

  public Saver() {
    updateProgress(0, 1)
  }

  protected abstract BooleanClass save()

  protected BooleanClass call() {
    BooleanClass result = save()
    updateProgress(1, 1)
    return result
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

  public void setDictionary(D dictionary) {
    $dictionary = dictionary
  }

}