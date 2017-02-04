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

  private ObjectMapper $mapper
  private Connection $connection

  public DatabaseDictionaryLoader(DatabaseDictionary dictionary, String path) {
    super(dictionary, path)
    updateProgress(-1, 1)
  }

  protected ObservableList<DatabaseWord> call() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      StatementGroup statementGroup = StatementGroup.new()
      JsonFactory factory = $mapper.getFactory()
      JsonParser parser = factory.createParser(stream)
      Integer size = stream.available()
      try {
        setupConnection()
        setupTables()
        setupStatementGroup(statementGroup)
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
              insertWord(statementGroup, temporaryWord)
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
        insert(statementGroup)
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
        statement.addBatch("CREATE TABLE translation_main (id INT, entry_id INT, index INT, title VARCHAR(512))")
        statement.addBatch("CREATE TABLE translation_form (translation_id INT, entry_id INT, index INT, form LONG VARCHAR)")
        statement.addBatch("CREATE TABLE tag (entry_id INT, index INT, form VARCHAR(512))")
        statement.addBatch("CREATE TABLE content (entry_id INT, index INT, title VARCHAR(512), text LONG VARCHAR)")
        statement.addBatch("CREATE TABLE variation (entry_id INT, index INT, title VARCHAR(512), form VARCHAR(512))")
        statement.addBatch("CREATE TABLE relation (entry_id INT, index INT, title VARCHAR(512), referrence_id INT)")
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

  private void setupStatementGroup(StatementGroup statementGroup) {
    PreparedStatement entryStatement = $connection.prepareStatement("INSERT INTO entry (id, form) VALUES (?, ?)")
    PreparedStatement equivalentStatement = $connection.prepareStatement("INSERT INTO translation_main (id, entry_id, index, title) VALUES (?, ?, ?, ?)")
    PreparedStatement equivalentNameStatement = $connection.prepareStatement("INSERT INTO translation_form (translation_id, entry_id, index, form) VALUES (?, ?, ?, ?)")
    PreparedStatement tagStatement = $connection.prepareStatement("INSERT INTO tag (entry_id, index, form) VALUES (?, ?, ?)")
    PreparedStatement informationStatement = $connection.prepareStatement("INSERT INTO content (entry_id, index, title, text) VALUES (?, ?, ?, ?)")
    PreparedStatement variationStatement = $connection.prepareStatement("INSERT INTO variation (entry_id, index, title, form) VALUES (?, ?, ?, ?)")
    PreparedStatement relationStatement = $connection.prepareStatement("INSERT INTO relation (entry_id, index, title, referrence_id) VALUES (?, ?, ?, ?)")
    statementGroup.setEntryStatement(entryStatement)
    statementGroup.setEquivalentStatement(equivalentStatement)
    statementGroup.setEquivalentNameStatement(equivalentNameStatement)
    statementGroup.setTagStatement(tagStatement)
    statementGroup.setInformationStatement(informationStatement)
    statementGroup.setVariationStatement(variationStatement)
    statementGroup.setRelationStatement(relationStatement)
  }

  private void insert(StatementGroup statementGroup) {
    try {
      try {
        updateProgressByBatch(0, 7)
        statementGroup.getEntryStatement().executeBatch()
        updateProgressByBatch(1, 7)
        statementGroup.getEquivalentStatement().executeBatch()
        updateProgressByBatch(2, 7)
        statementGroup.getEquivalentNameStatement().executeBatch()
        updateProgressByBatch(3, 7)
        statementGroup.getTagStatement().executeBatch()
        updateProgressByBatch(4, 7)
        statementGroup.getInformationStatement().executeBatch()
        updateProgressByBatch(5, 7)
        statementGroup.getVariationStatement().executeBatch()
        updateProgressByBatch(6, 7)
        statementGroup.getRelationStatement().executeBatch()
        updateProgressByBatch(7, 7)
      } finally {
        statementGroup.close()
      }
      $connection.commit()
    } catch (SQLException exception) {
      $connection.rollback()
    }
  }

  private void insertWord(StatementGroup statementGroup, SlimeWord word) {
    insertEntry(statementGroup, word)
    insertEquivalents(statementGroup, word)
    insertTags(statementGroup, word)
    insertInformations(statementGroup, word)
    insertVariations(statementGroup, word)
    insertRelations(statementGroup, word)
  }

  private void insertEntry(StatementGroup statementGroup, SlimeWord word) {
    PreparedStatement statement = statementGroup.getEntryStatement()
    statement.setInt(1, word.getId())
    statement.setString(2, word.getName())
    statement.addBatch()
  }

  private void insertEquivalents(StatementGroup statementGroup, SlimeWord word) {
    PreparedStatement statement = statementGroup.getEquivalentStatement()
    PreparedStatement nameStatement = statementGroup.getEquivalentNameStatement()
    Integer equivalentId = 0
    for (Integer i : 0 ..< word.getRawEquivalents().size()) {
      SlimeEquivalent equivalent = word.getRawEquivalents()[i]
      statement.setInt(1, equivalentId)
      statement.setInt(2, word.getId())
      statement.setInt(3, i)
      statement.setString(4, equivalent.getTitle())
      statement.addBatch()
      for (Integer j : 0 ..< equivalent.getNames().size()) {
        String name = equivalent.getNames()[j]
        nameStatement.setInt(1, equivalentId)
        nameStatement.setInt(2, word.getId())
        nameStatement.setInt(3, j)
        nameStatement.setString(4, name)
        nameStatement.addBatch()
      }
      equivalentId ++
    }
  }

  private void insertTags(StatementGroup statementGroup, SlimeWord word) {
    PreparedStatement statement = statementGroup.getTagStatement()
    for (Integer i : 0 ..< word.getTags().size()) {
      String tag = word.getTags()[i]
      statement.setInt(1, word.getId())
      statement.setInt(2, i)
      statement.setString(3, tag)
      statement.addBatch()
    }
  }

  private void insertInformations(StatementGroup statementGroup, SlimeWord word) {
    PreparedStatement statement = statementGroup.getInformationStatement()
    for (Integer i : 0 ..< word.getInformations().size()) {
      SlimeInformation information = word.getInformations()[i]
      statement.setInt(1, word.getId())
      statement.setInt(2, i)
      statement.setString(3, information.getTitle())
      statement.setString(4, information.getText())
      statement.addBatch()
    }
  }

  private void insertVariations(StatementGroup statementGroup, SlimeWord word) {
    PreparedStatement statement = statementGroup.getVariationStatement()
    for (Integer i : 0 ..< word.getVariations().size()) {
      SlimeVariation variation = word.getVariations()[i]
      statement.setInt(1, word.getId())
      statement.setInt(2, i)
      statement.setString(3, variation.getTitle())
      statement.setString(4, variation.getName())
      statement.addBatch()
    }
  }

  private void insertRelations(StatementGroup statementGroup, SlimeWord word) {
    PreparedStatement statement = statementGroup.getRelationStatement()
    for (Integer i : 0 ..< word.getRelations().size()) {
      SlimeRelation variation = word.getRelations()[i]
      statement.setInt(1, word.getId())
      statement.setInt(2, i)
      statement.setString(3, variation.getTitle())
      statement.setInt(4, variation.getId())
      statement.addBatch()
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