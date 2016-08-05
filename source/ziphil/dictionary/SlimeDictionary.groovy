package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.function.Consumer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import net.arnx.jsonic.JSON
import net.arnx.jsonic.JSONEventType
import net.arnx.jsonic.JSONException
import net.arnx.jsonic.JSONReader
import net.arnx.jsonic.JSONWriter
import net.arnx.jsonic.TypeReference
import ziphil.module.Setting
import ziphil.module.Strings


@CompileStatic @Newify
public class SlimeDictionary extends Dictionary<SlimeWord, SlimeSuggestion> {

  private Consumer<Integer> $onLinkClicked
  private Map<String, Object> $externalData = HashMap.new()

  public SlimeDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
    setupSuggestions()
  }

  public SlimeDictionary(String name, String path, ObservableList<SlimeWord> words) {
    super(name, path)
    $words.addAll(words)
    setupWords()
    setupSuggestions()
  }

  protected Boolean checkSuggestion(SlimeWord word, String search) {
    Setting setting = Setting.getInstance()
    Boolean ignoresAccent = setting.getIgnoresAccent()
    Boolean ignoresCase = setting.getIgnoresCase()
    Boolean existsSuggestion = false
    word.getVariations().each() { SlimeVariation variation ->
      String variationTitle = variation.getTitle()
      String variationName = variation.getName()
      String newVariationName = variationName
      String newSearch = search
      if (ignoresAccent) {
        newVariationName = Strings.unaccent(newVariationName)
        newSearch = Strings.unaccent(newSearch)
      }
      if (ignoresCase) {
        newVariationName = Strings.toLowerCase(newVariationName)
        newSearch = Strings.toLowerCase(newSearch)
      }
      if (newVariationName == newSearch) {
        SlimePossibility possibility = SlimePossibility.new(word, variationTitle)
        $suggestions[0].getPossibilities().add(possibility)
        $suggestions[0].update()
        existsSuggestion = true
      }
    }
    return existsSuggestion
  }

  public void searchDetail(SlimeSearchParameter parameter) {
    Integer searchId = parameter.getId()
    String searchName = parameter.getName()
    SearchType nameSearchType = parameter.getNameSearchType()
    String searchEquivalentName = parameter.getEquivalentName()
    String searchEquivalentTitle = parameter.getEquivalentTitle()
    SearchType equivalentSearchType = parameter.getEquivalentSearchType()
    String searchInformationText = parameter.getInformationText()
    String searchInformationTitle = parameter.getInformationTitle()
    SearchType informationSearchType = parameter.getInformationSearchType()
    String searchTag = parameter.getTag()
    $filteredWords.setPredicate() { SlimeWord word ->
      Boolean predicate = true
      Integer id = word.getId()
      String name = word.getName()
      List<SlimeEquivalent> equivalents = word.getRawEquivalents()
      List<SlimeInformation> informations = word.getInformations()
      List<String> tags = word.getTags()
      if (searchId != null) {
        if (id != searchId) {
          predicate = false
        }
      }
      if (searchName != null) {
        if (!SearchType.matches(nameSearchType, name, searchName)) {
          predicate = false
        }
      }
      if (searchEquivalentName != null) {
        Boolean equivalentPredicate = false
        equivalents.each() { SlimeEquivalent equivalent ->
          String equivalentTitle = equivalent.getTitle()
          equivalent.getNames().each() { String equivalentName ->
            if (SearchType.matches(equivalentSearchType, equivalentName, searchEquivalentName) && (searchEquivalentTitle == null || equivalentTitle == searchEquivalentTitle)) {
              equivalentPredicate = true
            }
          }
        }
        if (!equivalentPredicate) {
          predicate = false
        }
      }
      if (searchInformationText != null) {
        Boolean informationPredicate = false
        informations.each() { SlimeInformation information ->
          String informationText = information.getText()
          String informationTitle = information.getTitle()
          if (SearchType.matches(informationSearchType, informationText, searchInformationText) && (searchInformationTitle == null || informationTitle == searchInformationTitle)) {
            informationPredicate = true
          }
        }
        if (!informationPredicate) {
          predicate = false
        }
      }
      if (searchTag != null) {
        Boolean tagPredicate = false
        tags.each() { String tag ->
          if (tag == searchTag) {
            tagPredicate = true
          }
        }
        if (!tagPredicate) {
          predicate = false
        }
      }
      return predicate
    }
    $filteredSuggestions.setPredicate() { SlimeSuggestion suggestion ->
      return false
    }
  }

  public void modifyWord(SlimeWord oldWord, SlimeWord newWord) {
    if (containsId(newWord.getId(), newWord)) {
      newWord.setId(validMinId())
    }
    if (oldWord.getId() != newWord.getId() || oldWord.getName() != newWord.getName()) {
      $words.each() { SlimeWord registeredWord ->
        registeredWord.getRelations().each() { SlimeRelation relation ->
          if (relation.getId() == oldWord.getId()) {
            relation.setId(newWord.getId())
            relation.setName(newWord.getName())
          }
        }
      }
    }
    newWord.createContentPane()
  }

  public void addWord(SlimeWord word) {
    if (containsId(word.getId(), word)) {
      word.setId(validMinId())
    }
    word.setDictionary(this)
    $words.add(word)
  }

  public void removeWord(SlimeWord word) {
    $words.each() { SlimeWord registeredWord ->
      registeredWord.getRelations().removeAll{relation -> relation.getId() == word.getId()}
    }
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

  public Boolean containsId(Integer id, SlimeWord excludedWord) {
    return $words.any{word -> word != excludedWord && word.getId() == id}
  }

  public SlimeDictionary copy() {
    ObservableList<SlimeWord> copiedWords = FXCollections.observableArrayList($words)
    SlimeDictionary dictionary = SlimeDictionary.new($name, $path, copiedWords)
    return dictionary
  }

  private void load() {
    if ($path != null) {
      FileInputStream stream = FileInputStream.new($path)
      JSON json = JSON.new()
      JSONReader reader = json.getReader(stream)
      JSONEventType type = reader.next()
      while (type != null) {
        if (type == JSONEventType.NAME) {
          String keyName = reader.getString()
          reader.next()
          if (keyName == "words") {
            TypeReference<List<SlimeWord>> typeReference = SlimeTypeReference.new()
            List<SlimeWord> words = (List)reader.getValue(typeReference)
            words.each() { SlimeWord word ->
              word.setDictionary(this)
            }
            $words.addAll(words)
          } else {
            $externalData.put(keyName, reader.getValue(Object))
          }
        }
        type = reader.next()
      }
      stream.close()
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
    $externalData.each() { String keyName, Object object ->
      writer.name(keyName).value(object)
    }
    writer.endObject()
    stream.close()
  }

  private void setupWords() {
    $sortedWords.setComparator() { SlimeWord firstWord, SlimeWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

  private void setupSuggestions() {
    SlimeSuggestion suggestion = SlimeSuggestion.new()
    suggestion.setDictionary(this)
    $suggestions.add(suggestion)
  }

  public Consumer<Integer> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<Integer> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

}


protected class SlimeTypeReference extends TypeReference<List<SlimeWord>> {
}