package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import net.arnx.jsonic.JSON
import net.arnx.jsonic.JSONEventType
import net.arnx.jsonic.JSONException
import net.arnx.jsonic.JSONReader
import net.arnx.jsonic.JSONWriter
import net.arnx.jsonic.TypeReference
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public class SlimeDictionary extends Dictionary<SlimeWord> {

  private String $name = ""
  private String $path = ""
  private ObservableList<SlimeWord> $words = FXCollections.observableArrayList()
  private FilteredList<SlimeWord> $filteredWords
  private SortedList<SlimeWord> $sortedWords

  public SlimeDictionary(String name, String path) {
    $name = name
    $path = path
    load()
    setupWords()
  }

  public SlimeDictionary(String name, String path, ObservableList<SlimeWord> words) {
    $name = name
    $path = path
    $words = words
    setupWords()
  }

  public void searchByName(String search, Boolean isStrict) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { SlimeWord word ->
        if (isStrict) {
          String newName = word.getName()
          String newSearch = search
          if (ignoresAccent) {
            newName = Strings.unaccent(newName)
            newSearch = Strings.unaccent(newSearch)
          }
          if (ignoresCase) {
            newName = Strings.toLowerCase(newName)
            newSearch = Strings.toLowerCase(newSearch)
          }
          return newName.startsWith(newSearch)
        } else {
          Matcher matcher = pattern.matcher(word.getName())
          return matcher.find()
        }
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  public void searchByEquivalent(String search, Boolean isStrict) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { SlimeWord word ->
        if (isStrict) {
          return word.getEquivalents().any() { String equivalent ->
            return equivalent.startsWith(search)
          }
        } else {
          return word.getEquivalents().any() { String equivalent ->
            Matcher matcher = pattern.matcher(equivalent)
            return matcher.find()
          }
        }
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  public void searchByContent(String search) {
    try {
      Pattern pattern = Pattern.compile(search)
      $filteredWords.setPredicate() { SlimeWord word ->
        Matcher matcher = pattern.matcher(word.getContent())
        return matcher.find()
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  public void modifyWord(SlimeWord oldWord, SlimeWord newWord) {
    newWord.createContentPane()
  }

  public void addWord(SlimeWord word) {
    $words.add(word)
  }

  public void removeWord(SlimeWord word) {
    $words.remove(word)
  }

  public SlimeWord emptyWord() {
    SlimeWord word = SlimeWord.new()
    word.setId(validMinId())
    return word
  }

  public SlimeWord copiedWord(SlimeWord oldWord) {
    Integer id = oldWord.getId()
    String name = oldWord.getName()
    List<SlimeEquivalent> rawEquivalents = oldWord.getRawEquivalents()
    List<String> tags = oldWord.getTags()
    List<SlimeInformation> informations = oldWord.getInformations()
    List<SlimeVariation> variations = oldWord.getVariations()
    List<SlimeRelation> relations = oldWord.getRelations()
    SlimeWord newWord = SlimeWord.new(id, name, rawEquivalents, tags, informations, variations, relations)
    return newWord
  }

  public SlimeWord inheritedWord(SlimeWord oldWord) {
    Integer id = validMinId()
    String name = oldWord.getName()
    List<SlimeEquivalent> rawEquivalents = oldWord.getRawEquivalents()
    List<String> tags = oldWord.getTags()
    List<SlimeInformation> informations = oldWord.getInformations()
    List<SlimeVariation> variations = oldWord.getVariations()
    List<SlimeRelation> relations = oldWord.getRelations()
    SlimeWord newWord = SlimeWord.new(id, name, rawEquivalents, tags, informations, variations, relations)
    return newWord
  }

  public Integer validMinId() {
    Integer minId = 0
    $words.each() { SlimeWord word ->
      if (minId < word.getId()) {
        minId = word.getId()
      }
    }
    return minId + 1
  }

  public List<String> registeredTags() {
    List<String> tags = ArrayList.new()
    $words.each() { SlimeWord word ->
      word.getTags().each() { String tag ->
        if (!tags.contains(tag)) {
          tags.add(tag)
        }
      }
    }
    return tags
  }

  public List<String> registeredEquivalentTitles() {
    List<String> titles = ArrayList.new()
    $words.each() { SlimeWord word ->
      word.getRawEquivalents().each() { SlimeEquivalent equivalent ->
        if (!titles.contains(equivalent.getTitle())) {
          titles.add(equivalent.getTitle())
        }
      }
    }
    return titles
  }

  public List<String> registeredInformationTitles() {
    List<String> titles = ArrayList.new()
    $words.each() { SlimeWord word ->
      word.getInformations().each() { SlimeInformation information ->
        if (!titles.contains(information.getTitle())) {
          titles.add(information.getTitle())
        }
      }
    }
    return titles
  }

  public List<String> registeredVariationTitles() {
    List<String> titles = ArrayList.new()
    $words.each() { SlimeWord word ->
      word.getVariations().each() { SlimeVariation variation ->
        if (!titles.contains(variation.getTitle())) {
          titles.add(variation.getTitle())
        }
      }
    }
    return titles
  }

  public List<String> registeredRelationTitles() {
    List<String> titles = ArrayList.new()
    $words.each() { SlimeWord word ->
      word.getRelations().each() { SlimeRelation relation ->
        if (!titles.contains(relation.getTitle())) {
          titles.add(relation.getTitle())
        }
      }
    }
    return titles
  }

  public SlimeDictionary copy() {
    ObservableList<SlimeWord> copiedWords = FXCollections.observableArrayList()
    copiedWords.addAll($words)
    SlimeDictionary dictionary = SlimeDictionary.new($name, $path, copiedWords)
    return dictionary
  }

  private void load() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      JSON json = JSON.new()
      JSONReader reader = json.getReader(stream)
      JSONEventType type
      while ((type = reader.next()) != null) {
        if (type == JSONEventType.NAME) {
          String keyName = reader.getString()
          if (keyName == "words") {
            reader.next()
            TypeReference<List<SlimeWord>> typeReference = SlimeTypeReference.new()
            List<SlimeWord> words = (List)(reader.getValue(typeReference))
            $words.addAll(words)
          }
        }
      }
    }
  }

  public void save() {
    FileOutputStream stream = FileOutputStream.new($path)
    JSON json = JSON.new()
    json.setPrettyPrint(true)
    json.setIndentText("  ")
    JSONWriter writer = json.getWriter(stream)
    writer.beginObject()
    writer.name("words")
    writer.beginArray()
    $words.each() { SlimeWord word ->
      writer.beginObject()
      writer.name("entry").value(word.getEntry())
      writer.name("translations").value(word.getRawEquivalents())
      writer.name("tags").value(word.getTags())
      writer.name("contents").value(word.getInformations())
      writer.name("variations").value(word.getVariations())
      writer.name("relations").value(word.getRelations())
      writer.endObject()
    }
    writer.endArray()
    writer.endObject()
    stream.close()
  }

  private void setupWords() {
    $filteredWords = FilteredList.new($words)
    $sortedWords = SortedList.new($filteredWords)
    $sortedWords.setComparator() { SlimeWord firstWord, SlimeWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

  public Boolean supportsEquivalent() {
    return true
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

  public ObservableList<SlimeWord> getWords() {
    return $sortedWords
  }

  public ObservableList<SlimeWord> getRawWords() {
    return $words
  }

}


protected class SlimeTypeReference extends TypeReference<List<SlimeWord>> {
}