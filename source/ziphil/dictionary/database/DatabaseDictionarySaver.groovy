package ziphil.dictionary.database

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import ziphil.dictionary.DictionarySaver
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseDictionarySaver extends DictionarySaver<DatabaseDictionary> {

  private ObjectMapper $mapper

  public DatabaseDictionarySaver(DatabaseDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected Boolean call() {
    if ($path != null) {
    }
    return true
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

}