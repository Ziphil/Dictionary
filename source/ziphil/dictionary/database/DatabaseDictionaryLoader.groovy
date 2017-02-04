package ziphil.dictionary.database

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import javafx.collections.ObservableList
import org.apache.derby.jdbc.EmbeddedDriver
import ziphil.Launcher
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeVariation
import ziphil.dictionary.slime.SlimeWord
import ziphil.module.CustomFiles
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class DatabaseDictionaryLoader extends DictionaryLoader<DatabaseDictionary, DatabaseWord> {

  private static final String DATABASE_DIRECTORY = "temp/database/"

  private PreparedStatement $entryStatement
  private PreparedStatement $equivalentStatement
  private PreparedStatement $equivalentNameStatement
  private PreparedStatement $tagStatement
  private PreparedStatement $informationStatement
  private PreparedStatement $variationStatement
  private PreparedStatement $relationStatement
  private ObjectMapper $mapper
  private Connection $connection

  public DatabaseDictionaryLoader(DatabaseDictionary dictionary, String path) {
    super(dictionary, path)
    updateProgress(-1, 1)
  }

  protected ObservableList<DatabaseWord> call() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      JsonFactory factory = $mapper.getFactory()
      JsonParser parser = factory.createParser(stream)
      Integer size = stream.available()
      try {
        setupConnection()
        setupTables()
        setupStatements()
        parser.nextToken()
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
          String topFieldName = parser.getCurrentName()
          parser.nextToken()
          if (topFieldName == "words") {
            while (parser.nextToken() == JsonToken.START_OBJECT) {
              SlimeWord temporaryWord = SlimeWord.new()
              DatabaseWord word = DatabaseWord.new()
              if (isCancelled()) {
                return null
              }
              parseWord(parser, temporaryWord)
              insertWord(temporaryWord)
              word.setId(temporaryWord.getId())
              word.setName(temporaryWord.getName())
              word.setDictionary($dictionary)
              $words.add(word)
              updateProgressByParser(parser, size)
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
              updateProgressByParser(parser, size)
            }
          } else {
            println("Extension data will not be saved")
          }
        }
        insert()
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

  private void setupTables() {
    try {
      Statement statement = $connection.createStatement()
      try {
        statement.addBatch("CREATE TABLE entry (id INT, form VARCHAR(512))")
        statement.addBatch("CREATE TABLE translation_main (id INT, entry_id INT, title VARCHAR(512))")
        statement.addBatch("CREATE TABLE translation_form (translation_id INT, entry_id INT, form LONG VARCHAR)")
        statement.addBatch("CREATE TABLE tag (entry_id INT, form VARCHAR(512))")
        statement.addBatch("CREATE TABLE content (entry_id INT, title VARCHAR(512), text LONG VARCHAR)")
        statement.addBatch("CREATE TABLE variation (entry_id INT, title VARCHAR(512), form VARCHAR(512))")
        statement.addBatch("CREATE TABLE relation (entry_id INT, title VARCHAR(512), referrence_id INT)")
        statement.executeBatch()
      } finally {
        statement.close()
      }
      $connection.commit()
    } catch (SQLException exception) {
      $connection.rollback()
      throw exception
    }
  }

  private void setupStatements() {
    $entryStatement = $connection.prepareStatement("INSERT INTO entry (id, form) VALUES (?, ?)")
    $equivalentStatement = $connection.prepareStatement("INSERT INTO translation_main (id, entry_id, title) VALUES (?, ?, ?)")
    $equivalentNameStatement = $connection.prepareStatement("INSERT INTO translation_form (translation_id, entry_id, form) VALUES (?, ?, ?)")
    $tagStatement = $connection.prepareStatement("INSERT INTO tag (entry_id, form) VALUES (?, ?)")
    $informationStatement = $connection.prepareStatement("INSERT INTO content (entry_id, title, text) VALUES (?, ?, ?)")
    $variationStatement = $connection.prepareStatement("INSERT INTO variation (entry_id, title, form) VALUES (?, ?, ?)")
    $relationStatement = $connection.prepareStatement("INSERT INTO relation (entry_id, title, referrence_id) VALUES (?, ?, ?)")
  }

  private void insert() {
    try {
      try {
        updateProgressByBatch(0, 7)
        $entryStatement.executeBatch()
        updateProgressByBatch(1, 7)
        $equivalentStatement.executeBatch()
        updateProgressByBatch(2, 7)
        $equivalentNameStatement.executeBatch()
        updateProgressByBatch(3, 7)
        $tagStatement.executeBatch()
        updateProgressByBatch(4, 7)
        $informationStatement.executeBatch()
        updateProgressByBatch(5, 7)
        $variationStatement.executeBatch()
        updateProgressByBatch(6, 7)
        $relationStatement.executeBatch()
        updateProgressByBatch(7, 7)
      } finally {
        $entryStatement.close()
        $equivalentStatement.close()
        $equivalentNameStatement.close()
        $tagStatement.close()
        $informationStatement.close()
        $variationStatement.close()
        $relationStatement.close()
      }
      $connection.commit()
    } catch (SQLException exception) {
      $connection.rollback()
    }
  }

  private void insertWord(SlimeWord word) {
    insertEntry(word)
    insertEquivalents(word)
    insertTags(word)
    insertInformations(word)
    insertVariations(word)
    insertRelations(word)
  }

  private void insertEntry(SlimeWord word) {
    $entryStatement.setInt(1, word.getId())
    $entryStatement.setString(2, word.getName())
    $entryStatement.addBatch()
  }

  private void insertEquivalents(SlimeWord word) {
    Integer id = word.getId()
    Integer translationId = 0
    for (Integer i : 0 ..< word.getRawEquivalents().size()) {
      SlimeEquivalent equivalent = word.getRawEquivalents()[i]
      $equivalentStatement.setInt(1, translationId)
      $equivalentStatement.setInt(2, id)
      $equivalentStatement.setString(3, equivalent.getTitle())
      $equivalentStatement.addBatch()
      for (Integer j : 0 ..< equivalent.getNames().size()) {
        String name = equivalent.getNames()[j]
        $equivalentNameStatement.setInt(1, translationId)
        $equivalentNameStatement.setInt(2, id)
        $equivalentNameStatement.setString(3, name)
        $equivalentNameStatement.addBatch()
      }
      translationId ++
    }
  }

  private void insertTags(SlimeWord word) {
    Integer id = word.getId()
    for (Integer i : 0 ..< word.getTags().size()) {
      String tag = word.getTags()[i]
      $tagStatement.setInt(1, id)
      $tagStatement.setString(2, tag)
      $tagStatement.addBatch()
    }
  }

  private void insertInformations(SlimeWord word) {
    Integer id = word.getId()
    for (Integer i : 0 ..< word.getInformations().size()) {
      SlimeInformation information = word.getInformations()[i]
      $informationStatement.setInt(1, id)
      $informationStatement.setString(2, information.getTitle())
      $informationStatement.setString(3, information.getText())
      $informationStatement.addBatch()
    }
  }

  private void insertVariations(SlimeWord word) {
    Integer id = word.getId()
    for (Integer i : 0 ..< word.getVariations().size()) {
      SlimeVariation variation = word.getVariations()[i]
      $variationStatement.setInt(1, id)
      $variationStatement.setString(2, variation.getTitle())
      $variationStatement.setString(3, variation.getName())
      $variationStatement.addBatch()
    }
  }

  private void insertRelations(SlimeWord word) {
    Integer id = word.getId()
    for (Integer i : 0 ..< word.getRelations().size()) {
      SlimeRelation variation = word.getRelations()[i]
      $relationStatement.setInt(1, id)
      $relationStatement.setString(2, variation.getTitle())
      $relationStatement.setInt(3, variation.getId())
      $relationStatement.addBatch()
    }
  }

  private void parseWord(JsonParser parser, SlimeWord word) {
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

  private void parseEntry(JsonParser parser, SlimeWord word) {
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

  private void parseEquivalents(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeEquivalent equivalent = SlimeEquivalent.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String equivalentFieldName = parser.getCurrentName()
        parser.nextToken()
        if (equivalentFieldName == "title") {
          String title = parser.getValueAsString()
          equivalent.setTitle(title)
        } else if (equivalentFieldName == "forms") {
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            String name = parser.getValueAsString()
            equivalent.getNames().add(name)
            word.getEquivalents().add(name)
          }
        }
      }
      word.getRawEquivalents().add(equivalent)
    }
  }

  private void parseTags(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() != JsonToken.END_ARRAY) {
      String tag = parser.getValueAsString()
      word.getTags().add(tag)
    }
  }

  private void parseInformations(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeInformation information = SlimeInformation.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String informationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (informationFieldName == "title") {
          String title = parser.getValueAsString()
          information.setTitle(title)
        } else if (informationFieldName == "text") {
          String text = parser.getValueAsString()
          information.setText(text)
        }
      }
      word.getInformations().add(information)
    }
  }

  private void parseVariations(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeVariation variation = SlimeVariation.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String variationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (variationFieldName == "title") {
          String title = parser.getValueAsString()
          variation.setTitle(title)
        } else if (variationFieldName == "form") {
          String name = parser.getValueAsString()
          variation.setName(name)
        }
      }
      word.getVariations().add(variation)
    }
  }

  private void parseRelations(JsonParser parser, SlimeWord word) {
    while (parser.nextToken() == JsonToken.START_OBJECT) {
      SlimeRelation relation = SlimeRelation.new()
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String relationFieldName = parser.getCurrentName()
        parser.nextToken()
        if (relationFieldName == "title") {
          String title = parser.getValueAsString()
          relation.setTitle(title)
        } else if (relationFieldName == "entry") {
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String relationEntryFieldName = parser.getCurrentName()
            parser.nextToken()
            if (relationEntryFieldName == "id") {
              Integer id = parser.getValueAsInt()
              relation.setId(id)
            } else if (relationEntryFieldName == "form") {
              String name = parser.getValueAsString()
              relation.setName(name)
            }
          }
        }
      }
      word.getRelations().add(relation)
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

  private void updateProgressByParser(JsonParser parser, Integer size) {
    if (parser != null) {
      Double progress = ((parser.getCurrentLocation().getByteOffset() * 3) / (size * 4)).toDouble()
      if (progress > 0.75) {
        updateProgress(0.75, 1)
      } else {
        updateProgress(progress, 1)
      }
    } else {
      updateProgress(-1, 1)
    }
  }

  private void updateProgressByBatch(Integer progress, Integer size) {
    Double newProgress = (progress / (size * 4) + 0.75).toDouble()
    updateProgress(newProgress, 1)
  }

  public void setMapper(ObjectMapper mapper) {
    $mapper = mapper
  }

  public void setConnection(Connection connection) {
    $connection = connection
  }

}