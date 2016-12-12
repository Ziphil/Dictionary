package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
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

}