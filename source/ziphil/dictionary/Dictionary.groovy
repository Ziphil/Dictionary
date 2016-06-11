package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList


@CompileStatic @Newify
public abstract class Dictionary {

  public abstract void searchByName(String search, Boolean isStrict)

  public abstract void searchByEquivalent(String search, Boolean isStrict)

  public abstract void searchByContent(String search)

  public abstract void save()

  public abstract Boolean supportsEquivalent()

  public abstract String getName()

  public abstract DictionaryType getType()

  public abstract ObservableList<? extends Word> getWords()

  public abstract ObservableList<? extends Word> getRawWords()

}