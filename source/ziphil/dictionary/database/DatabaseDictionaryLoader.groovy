package ziphil.dictionary.database

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseDictionaryLoader extends DictionaryLoader<DatabaseDictionary, DatabaseWord> {

  private ObjectMapper $mapper

  public DatabaseDictionaryLoader(DatabaseDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected ObservableList<DatabaseWord> call() {
    if ($path != null) {
    }
    return $words
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

}