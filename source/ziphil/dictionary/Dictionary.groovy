package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic 
public interface Dictionary<W extends Word> {

  public void searchByName(String search, Boolean isStrict)

  public void searchByEquivalent(String search, Boolean isStrict)

  public void searchByContent(String search)

  public void searchScript(String script)

  public void shuffleWords()

  public void update()

  public void updateMinimum()

  public void save()

  public Integer hitSize()

  public Integer totalSize()

  public String getName() 

  public void setName(String name)

  public String getPath()

  public void setPath(String path)

  public ObservableList<Element> getWholeWords()

  public ObservableList<W> getWords()

  public ObservableList<W> getRawWords()

  public String getExtension()

  public Task<?> getLoader()

  public Task<?> getSaver()

  public Boolean isChanged()

}