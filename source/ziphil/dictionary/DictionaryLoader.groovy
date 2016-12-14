package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryLoader<D extends Dictionary, W extends Word> extends Task<ObservableList<W>> {

  protected D $dictionary
  protected ObservableList<W> $words = FXCollections.observableArrayList()
  protected String $path

  public DictionaryLoader(D dictionary, String path) {
    $path = path
    $dictionary = dictionary
    setupEventHandler()
  }

  protected abstract ObservableList<W> call()

  protected void update() {
    $dictionary.getRawWords().addAll($words)
    $dictionary.update()
  }

  private void setupEventHandler() {
    addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      update()
    }
  }

}