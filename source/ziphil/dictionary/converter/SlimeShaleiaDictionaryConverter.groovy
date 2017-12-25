package ziphil.dictionary.converter

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.AlphabetOrderType
import ziphil.dictionary.DictionaryLoader
import ziphil.dictionary.Word
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
public class SlimeShaleiaDictionaryConverter extends DictionaryLoader<SlimeDictionary, SlimeWord> {

  private ShaleiaDictionary $sourceDictionary

  public SlimeShaleiaDictionaryConverter(ShaleiaDictionary sourceDictionary) {
    super()
    $sourceDictionary = sourceDictionary
  }

  protected BooleanClass load() {
    List<ShaleiaWord> sourceWords = $sourceDictionary.getRawWords()
    Map<String, IntegerClass> sourceIds = HashMap.new()
    Int size = sourceWords.size()
    for (Int i = 0 ; i < size ; i ++) {
      if (isCancelled()) {
        return false
      }
      ShaleiaWord sourceWord = sourceWords[i]
      if (!sourceWord.getName().startsWith("\$")) {
        SlimeWord word = SlimeWord.new()
        word.setId(i + 1)
        word.setName(sourceWord.getName())
        sourceIds.put(sourceWord.getName(), i + 1)
        ShaleiaDescriptionReader sourceReader = ShaleiaDescriptionReader.new(sourceWord.getDescription())
        try {
          while (sourceReader.readLine() != null) {
            if (sourceReader.findCreationDate()) {
              String sourceTotalPart = sourceReader.lookupTotalPart()
              addTotalPart(word, sourceTotalPart)
            }
            if (sourceReader.findEquivalent()) {
              String sourcePart = sourceReader.lookupPart()
              String sourceEquivalent = sourceReader.lookupEquivalent()
              addEquivalent(word, sourcePart, sourceEquivalent)
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
            String sourceSynonym = sourceReader.lookupSynonym()
            addSynonym(word, sourceSynonym, sourceIds)
          }
        }
      } finally {
        sourceReader.close()
      }
      updateProgress(i + size + 1, size * 2)
    }
    $dictionary.setAlphabetOrder($sourceDictionary.getAlphabetOrder())
    $dictionary.setAlphabetOrderType(AlphabetOrderType.CUSTOM) 
    $dictionary.setAkrantiainSource($sourceDictionary.getAkrantiainSource())
    return true
  }

  private void addTotalPart(SlimeWord word, String sourceTotalPart) {
    word.getTags().add(sourceTotalPart)
  }

  private void addEquivalent(SlimeWord word, String sourcePart, String sourceEquivalent) {
    String nextSourceEquivalent = sourceEquivalent
    nextSourceEquivalent = nextSourceEquivalent.replaceAll(/(\{|\}|\[|\]|\/)/, "")
    nextSourceEquivalent = nextSourceEquivalent.replaceAll(/&#x([0-9A-Fa-f]+);/) { String all, String codePoint ->
      return CharacterClass.toChars(IntegerClass.parseInt(codePoint, 16))[0]
    }
    SlimeEquivalent equivalent = SlimeEquivalent.new()
    equivalent.setTitle(sourcePart)
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

  private void addSynonym(SlimeWord word, String sourceSynonym, Map<String, IntegerClass> sourceIds) {
    String nextSourceSynonym = sourceSynonym
    nextSourceSynonym = nextSourceSynonym.replaceAll(/(\{|\}|\*)/, "")
    for (String sourceSynonymName : nextSourceSynonym.split(/\s*(,|;)\s*/)) {
      IntegerClass sourceId = sourceIds[sourceSynonymName]
      if (sourceId != null) {
        SlimeRelation relation = SlimeRelation.new()
        relation.setId(sourceId)
        relation.setName(sourceSynonymName)
        word.getRelations().add(relation)
      }
    }
  }

}