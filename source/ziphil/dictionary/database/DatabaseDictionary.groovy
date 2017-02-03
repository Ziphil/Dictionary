package ziphil.dictionary.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphil.dictionary.DictionaryBase
import ziphil.dictionary.Suggestion
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseDictionary extends DictionaryBase<DatabaseWord, Suggestion> {

  private static ObjectMapper $$mapper = createObjectMapper()

  public DatabaseDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
  }

  public void update() {
    $isChanged = true
  }

  public void updateMinimum() {
    $isChanged = true
  }

  private void setupWords() {
    $sortedWords.setComparator() { DatabaseWord firstWord, DatabaseWord secondWord ->
      Integer firstId = firstWord.getId()
      Integer secondId = secondWord.getId()
      String firstString = firstWord.getComparisonString()
      String secondString = secondWord.getComparisonString()
      Integer result = firstString <=> secondString
      if (result == 0) {
        return firstId <=> secondId
      } else {
        return result
      }
    }
  }

  protected Task<?> createLoader() {
    DatabaseDictionaryLoader loader = DatabaseDictionaryLoader.new(this, $path)
    loader.setMapper($$mapper)
    return loader
  }

  protected Task<?> createSaver() {
    DatabaseDictionarySaver saver = DatabaseDictionarySaver.new(this, $path)
    saver.setMapper($$mapper)
    return saver
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = ObjectMapper.new()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
  }

  public String getExtension() {
    return "json"
  }

}