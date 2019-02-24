package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.Loader
import ziphil.dictionary.Word
import ziphil.dictionary.WordOrderType
import ziphil.dictionary.shaleia.ShaleiaDescriptionReader
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeShaleiaConverter extends Loader<SlimeDictionary, SlimeWord> {

  private ShaleiaDictionary $sourceDictionary

  public SlimeShaleiaConverter(ShaleiaDictionary sourceDictionary) {
    super()
    $sourceDictionary = sourceDictionary
  }

  protected BooleanClass load() {
    List<ShaleiaWord> sourceWords = $sourceDictionary.getRawWords()
    Map<String, SlimeWord> correspondingWords = HashMap.new()
    Int size = sourceWords.size()
    for (Int i = 0 ; i < size ; i ++) {
      if (isCancelled()) {
        return false
      }
      ShaleiaWord sourceWord = sourceWords[i]
      if (!sourceWord.getUniqueName().startsWith("\$")) {
        SlimeWord word = SlimeWord.new()
        word.setNumber(i + 1)
        word.setName(sourceWord.getName())
        correspondingWords.put(sourceWord.getName(), word)
        ShaleiaDescriptionReader sourceReader = ShaleiaDescriptionReader.new(sourceWord.getDescription())
        try {
          while (sourceReader.readLine() != null) {
            if (sourceReader.findCreationDate()) {
              String sourceSort = sourceReader.lookupSort()
              addSort(word, sourceSort)
            }
            if (sourceReader.findEquivalent()) {
              String sourceCategory = sourceReader.lookupCategory()
              String sourceEquivalent = sourceReader.lookupEquivalent()
              addEquivalent(word, sourceCategory, sourceEquivalent)
            }
            if (sourceReader.findContent()) {
              String sourceTitle = sourceReader.title()
              String sourceContent = sourceReader.lookupContent()
              addContent(word, sourceTitle, sourceContent)
            }
          }
          word.setDictionary($dictionary)
          $words.add(word)
        } finally {
          sourceReader.close()
        }
      }
      updateProgress(i + 1, size * 2)
    }
    for (Int i = 0 ; i < size ; i ++) {
      if (isCancelled()) {
        return false
      }
      ShaleiaWord sourceWord = sourceWords[i]
      SlimeWord word = $words[i]
      ShaleiaDescriptionReader sourceReader = ShaleiaDescriptionReader.new(sourceWord.getDescription())
      try {
        while (sourceReader.readLine() != null) {
          if (sourceReader.findSynonym()) {
            String sourceSynonymType = sourceReader.lookupSynonymType()
            String sourceSynonym = sourceReader.lookupSynonym()
            addSynonym(word, sourceSynonymType, sourceSynonym, correspondingWords)
          }
        }
      } finally {
        sourceReader.close()
      }
      updateProgress(i + size + 1, size * 2)
    }
    $dictionary.setAlphabetOrder($sourceDictionary.getAlphabetOrder())
    $dictionary.setWordOrderType(WordOrderType.CUSTOM) 
    $dictionary.setAkrantiainSource($sourceDictionary.getAkrantiainSource())
    return true
  }

  private void addSort(SlimeWord word, String sourceSort) {
    word.getTags().add(sourceSort)
  }

  private void addEquivalent(SlimeWord word, String sourceCategory, String sourceEquivalent) {
    String nextSourceEquivalent = sourceEquivalent
    nextSourceEquivalent = nextSourceEquivalent.replaceAll(/(\{|\}|\[|\]|\/)/, "")
    nextSourceEquivalent = nextSourceEquivalent.replaceAll(/&#x([0-9A-Fa-f]+);/) { String all, String codePoint ->
      return CharacterClass.toChars(IntegerClass.parseInt(codePoint, 16))[0]
    }
    SlimeEquivalent equivalent = SlimeEquivalent.new()
    equivalent.setTitle(sourceCategory)
    equivalent.setNames(nextSourceEquivalent.split(/\s*,\s*/).toList())
    word.getRawEquivalents().add(equivalent)
  }

  private void addContent(SlimeWord word, String sourceTitle, String sourceContent) {
    String nextSourceContent = sourceContent
    nextSourceContent = nextSourceContent.replaceAll(/(\{|\}|\[|\]|\/)/, "")
    nextSourceContent = nextSourceContent.replaceAll(/&#x([0-9A-Fa-f]+);/) { String all, String codePoint ->
      return CharacterClass.toChars(IntegerClass.parseInt(codePoint, 16))[0]
    }
    SlimeInformation information = SlimeInformation.new()
    information.setTitle(sourceTitle)
    information.setText(nextSourceContent)
    word.getInformations().add(information)
  }

  private void addSynonym(SlimeWord word, String sourceSynonymType, String sourceSynonym, Map<String, SlimeWord> correspondingWords) {
    String nextSourceSynonym = sourceSynonym
    nextSourceSynonym = nextSourceSynonym.replaceAll(/(\{|\}|\*)/, "")
    for (String sourceSynonymName : nextSourceSynonym.split(/\s*(,|;)\s*/)) {
      if (correspondingWords.containsKey(sourceSynonymName)) {
        SlimeRelation relation = SlimeRelation.new()
        relation.setTitle(sourceSynonymType)
        relation.setWord(correspondingWords[sourceSynonymName])
        word.getRelations().add(relation)
      }
    }
  }

}