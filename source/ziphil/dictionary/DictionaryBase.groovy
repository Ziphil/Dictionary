package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.concurrent.Callable
import java.util.function.Consumer
import java.util.function.Predicate
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphil.custom.SimpleTask
import ziphil.custom.ShufflableList
import ziphil.module.Setting
import ziphil.module.Strings
import ziphil.module.akrantiain.Akrantiain
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class DictionaryBase<W extends Word, S extends Suggestion> implements Dictionary<W> {

  protected String $name = ""
  protected String $path = null
  protected ObservableList<W> $words = FXCollections.observableArrayList()
  protected FilteredList<W> $filteredWords
  protected SortedList<W> $sortedWords
  protected ShufflableList<W> $shufflableWords
  protected ObservableList<S> $suggestions = FXCollections.observableArrayList()
  protected FilteredList<S> $filteredSuggestions
  protected SortedList<S> $sortedSuggestions
  private ObservableList<Element> $wholeWords = FXCollections.observableArrayList()
  protected Consumer<SearchParameter> $onLinkClicked
  private Task<?> $loader
  private Task<?> $saver
  private DictionaryFactory $dictionaryFactory
  protected Boolean $changed = false
  protected Boolean $firstEmpty = false

  public DictionaryBase(String name, String path) {
    $name = name
    $path = path
    $changed = (path == null) ? true : false
    $firstEmpty = path == null
    setupSortedWords()
    setupWholeWords()
    prepare()
    load()
  }

  public DictionaryBase(String name, String path, DictionaryConverter converter) {
    $name = name
    $path = path
    $changed = true
    $firstEmpty = true
    setupSortedWords()
    setupWholeWords()
    prepare()
    convert(converter)
  }

  protected abstract void prepare()

  public void search(SearchParameter parameter) {
    ConjugationResolver conjugationResolver = null
    Exception suppressedException = null
    resetSuggestions()
    if (parameter instanceof NormalSearchParameter) {
      if (parameter.getSearchMode() == SearchMode.NAME) {
        conjugationResolver = createConjugationResolver()
        conjugationResolver.prepare(parameter)
      }
    }
    parameter.prepare(this)
    updateWordPredicate() { Word word ->
      try {
        if (conjugationResolver != null) {
          conjugationResolver.check(word)
        }
        if (suppressedException == null) {
          return parameter.matches(word)
        } else {
          return false
        }
      } catch (Exception exception) {
        suppressedException = exception
        return false
      }
    }
    if (suppressedException != null) {
      throw suppressedException
    }
  }

  private void updateWordPredicate(Predicate<? super W> predicate) {
    $filteredWords.setPredicate(predicate)
    $filteredSuggestions.setPredicate() { Suggestion suggestion ->
      return suggestion.isDisplayed()
    }
    $shufflableWords.unshuffle()
  }

  private void resetSuggestions() {
    for (Suggestion suggestion : $suggestions) {
      suggestion.getPossibilities().clear()
      suggestion.setDisplayed(false)
    }
  }

  public void shuffleWords() {
    $shufflableWords.shuffle()
  }

  public abstract void updateFirst()

  public abstract void updateMinimum()

  public abstract Object createPlainWord(W word)

  public abstract Dictionary copy()

  private void load() {
    DictionaryLoader loader = createLoader()
    if (loader != null) {
      loader.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
        if (!$firstEmpty) {
          $changed = false
        }
      }
      $loader = loader
      Thread thread = Thread.new(loader)
      thread.setDaemon(true)
      thread.start()
    } else {
      $loader = null
    }
  }

  private void convert(DictionaryConverter converter) {
    converter.setNewDictionary(this)
    if (converter != null) {
      converter.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
        if (!$firstEmpty) {
          $changed = false
        }
      }
      $loader = converter
      Thread thread = Thread.new(converter)
      thread.setDaemon(true)
      thread.start()
    } else {
      $loader = null
    }
  }

  public void save() {
    DictionarySaver saver = createSaver()
    if (saver != null) {
      $saver = saver
      saver.run()
      if (saver.getPath() != null) {
        $changed = false
      }
    } else {
      $saver = null
    }
  }

  public void saveBackup() {
    DictionarySaver saver = createSaver()
    if (saver != null) {
      if (saver.getPath() != null) {
        String newPath = saver.getPath().replaceAll(/(?=\.\w+$)/, "_backup")
        saver.setPath(newPath)
        saver.run()
      }
    }
  }

  public void export(ExportConfig config) {
    DictionaryExporter exporter = createExporter(config)
    if (exporter != null) {
      $saver = exporter
      exporter.run()
    } else {
      $saver = null
    }
  }

  private void setupSortedWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $shufflableWords = ShufflableList.new($sortedWords)
    $filteredSuggestions = FilteredList.new($suggestions){suggestion -> false}
    $sortedSuggestions = SortedList.new($filteredSuggestions)
  }

  private void setupWholeWords() {
    ListChangeListener<?> listener = (ListChangeListener){ Change<?> change ->
      $wholeWords.clear()
      $wholeWords.addAll($sortedSuggestions)
      $wholeWords.addAll($shufflableWords)
    }
    $filteredWords.addListener(listener)
    $filteredSuggestions.addListener(listener)
    $shufflableWords.addListener(listener)
  }

  public Int hitWordSize() {
    return $shufflableWords.size()
  }

  public Int totalWordSize() {
    return $words.size()
  }

  protected abstract ConjugationResolver createConjugationResolver()

  protected abstract DictionaryLoader createLoader()

  protected abstract DictionarySaver createSaver()

  protected abstract DictionaryExporter createExporter(ExportConfig config)

  public IndividualSetting createIndividualSetting() {
    return null
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

  public String getAlphabetOrder() {
    return null
  }

  public Akrantiain getAkrantiain() {
    return null
  }

  public ObservableList<Element> getWholeWords() {
    return $wholeWords
  }

  public ObservableList<W> getWords() {
    return $shufflableWords
  }

  public ObservableList<W> getRawWords() {
    return $words
  }

  public Consumer<SearchParameter> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<SearchParameter> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

  public Task<?> getLoader() {
    return $loader
  }

  public Task<?> getSaver() {
    return $saver
  }

  public DictionaryFactory getDictionaryFactory() {
    return $dictionaryFactory
  }

  public void setDictionaryFactory(DictionaryFactory dictionaryFactory) {
    $dictionaryFactory = dictionaryFactory
  }

  public Boolean isChanged() {
    return $changed
  }

}