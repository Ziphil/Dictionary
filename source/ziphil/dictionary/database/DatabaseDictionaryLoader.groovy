package ziphil.dictionary.database

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import java.sql.Connection
import java.sql.DriverManager
import javafx.collections.ObservableList
import org.apache.derby.jdbc.EmbeddedDriver
import ziphil.Launcher
import ziphil.dictionary.DictionaryLoader
import ziphil.module.CustomFiles
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseDictionaryLoader extends DictionaryLoader<DatabaseDictionary, DatabaseWord> {

  private static final String DATABASE_DIRECTORY = "temp/database/"

  private ObjectMapper $mapper
  private Connection $connection

  public DatabaseDictionaryLoader(DatabaseDictionary dictionary, String path) {
    super(dictionary, path)
    updateProgress(null, null)
  }

  protected ObservableList<DatabaseWord> call() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      JsonFactory factory = $mapper.getFactory()
      JsonParser parser = factory.createParser(stream)
      Integer size = stream.available()
      try {
        setupConnection()
        parser.nextToken()
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
          String topFieldName = parser.getCurrentName()
          parser.nextToken()
          if (topFieldName == "words") {
            while (parser.nextToken() == JsonToken.START_OBJECT) {
              DatabaseWord word = DatabaseWord.new()
              if (isCancelled()) {
                return null
              }
              parseWord(parser, word)
              word.setDictionary($dictionary)
              $words.add(word)
              updateProgress(parser, size)
            }
          } else if (topFieldName == "zpdic") {
            while (parser.nextToken() == JsonToken.FIELD_NAME) {
              String specialFieldName = parser.getCurrentName()
              parser.nextToken()
              if (specialFieldName == "alphabetOrder") {
                parseAlphabetOrder(parser)
              } else if (specialFieldName == "plainInformationTitles") {
                parsePlainInformationTitles(parser)
              } else if (specialFieldName == "informationTitleOrder") {
                parseInformationTitleOrder(parser)
              } else if (specialFieldName == "defaultWord") {
                parseDefaultWord(parser)
              }
              updateProgress(parser, size)
            }
          } else {
            println("Extension data will not be saved")
          }
        }
      } finally {
        parser.close()
        stream.close()
      }
    }
    return $words
  }

  private void setupConnection() {
    if ($connection == null) {
      String path = Launcher.BASE_PATH + DATABASE_DIRECTORY
      CustomFiles.deleteAll(File.new(path))
      DriverManager.registerDriver(EmbeddedDriver.new())
      $connection = DriverManager.getConnection("jdbc:derby:${path};create=true")
      $connection.setAutoCommit(false)
      $dictionary.setConnection($connection)
    }
    updateProgress(0, 1)
  }

  private void parseWord(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String wordFieldName = parser.getCurrentName()
      parser.nextToken()
      if (wordFieldName == "entry") {
        parseEntry(parser, word)
      } else if (wordFieldName == "translations") {
        parseEquivalents(parser, word)
      } else if (wordFieldName == "tags") {
        parseTags(parser, word)
      } else if (wordFieldName == "contents") {
        parseInformations(parser, word)
      } else if (wordFieldName == "variations") {
        parseVariations(parser, word)
      } else if (wordFieldName == "relations") {
        parseRelations(parser, word)
      }
    }
  }

  private void parseEntry(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String entryFieldName = parser.getCurrentName()
      parser.nextToken()
      if (entryFieldName == "id") {
        Integer id = parser.getValueAsInt()
        word.setId(id)
      } else if (entryFieldName == "form") {
        String name = parser.getValueAsString()
        word.setName(name)
      }
    }
  }

  private void parseEquivalents(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String equivalentFieldName = parser.getCurrentName()
        parser.nextToken()
        if (equivalentFieldName == "title") {
          String title = parser.getValueAsString()
        } else if (equivalentFieldName == "forms") {
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            String name = parser.getValueAsString()
          }
        }
      }
    }
  }

  private void parseTags(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      String tag = parser.getValueAsString()
    }
  }

  private void parseInformations(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String informationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (informationFieldName == "title") {
          String title = parser.getValueAsString()
        } else if (informationFieldName == "text") {
          String text = parser.getValueAsString()
        }
      }
    }
  }

  private void parseVariations(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String variationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (variationFieldName == "title") {
          String title = parser.getValueAsString()
        } else if (variationFieldName == "form") {
          String name = parser.getValueAsString()
        }
      }
    }
  }

  private void parseRelations(JsonParser parser, DatabaseWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String relationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (relationFieldName == "title") {
          String title = parser.getValueAsString()
        } else if (relationFieldName == "entry") {
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String relationEntryFieldName = parser.getCurrentName()
            parser.nextToken()
            if (relationEntryFieldName == "id") {
              Integer id = parser.getValueAsInt()
            } else if (relationEntryFieldName == "form") {
              String name = parser.getValueAsString()
            }
          }
        }
      }
    }
  }

  private void parseAlphabetOrder(JsonParser parser) {
    String alphabetOrder = parser.getValueAsString()
  }

  private void parsePlainInformationTitles(JsonParser parser) {
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      String title = parser.getValueAsString()
    }
  }

  private void parseInformationTitleOrder(JsonParser parser) {
    if (parser.getCurrentToken() != JsonToken.VALUE_NULL) {
      while (parser.nextToken() != JsonToken.END_ARRAY) {
        String title = parser.getValueAsString()
      }
    }
  }

  private void parseDefaultWord(JsonParser parser) {
  }

  private void updateProgress(JsonParser parser, Integer size) {
    if (parser != null) {
      updateProgress(parser.getCurrentLocation().getByteOffset(), size)
    } else {
      updateProgress(-1, 1)
    }
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

  public void setConnection(Connection connection) {
    $connection = connection
  }

}