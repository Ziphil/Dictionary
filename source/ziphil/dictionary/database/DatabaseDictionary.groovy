package ziphil.dictionary.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javafx.concurrent.Task
import ziphil.Launcher
import ziphil.dictionary.DictionaryBase
import ziphil.dictionary.Suggestion
import ziphil.module.CustomFiles
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseDictionary extends DictionaryBase<DatabaseWord, Suggestion> implements Closeable {

  private static final String DATABASE_DIRECTORY = "temp/database"
  private static final String LOG_PATH = "data/log/derby.log"

  private static ObjectMapper $$mapper = createObjectMapper()

  private Connection $connection

  public DatabaseDictionary(String name, String path) {
    super(name, path)
    setupConnection()
    load()
    setupWords()
  }

  public void update() {
    $isChanged = true
  }

  public void updateMinimum() {
    $isChanged = true
  }

  public void close() {
    $connection.close()
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true")
      throw SQLException.new("Shutdown not completed")
    } catch (SQLException exception) {
      if (exception.getSQLState() != "XJ015") {
        throw exception
      } else {
        println("Database successfully closed")
      }
    }
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

  private void setupConnection() {
    String path = Launcher.BASE_PATH + DATABASE_DIRECTORY
    CustomFiles.deleteAll(File.new(path))
    System.setProperty("derby.stream.error.file", Launcher.BASE_PATH + LOG_PATH)
    $connection = DriverManager.getConnection("jdbc:derby:${path};create=true")
    $connection.setAutoCommit(false)
  }

  protected Task<?> createLoader() {
    DatabaseDictionaryLoader loader = DatabaseDictionaryLoader.new(this, $path)
    loader.setMapper($$mapper)
    loader.setConnection($connection)
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