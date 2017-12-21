package ziphil.dictionary.slime.converter

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.shaleia.ShaleiaDescriptionReader
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeEquivalent
import ziphil.dictionary.slime.SlimeInformation
import ziphil.dictionary.slime.SlimeWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeShaleiaDictionaryConverter extends DictionaryConverter<SlimeDictionary, ShaleiaDictionary, SlimeWord> {

  public SlimeShaleiaDictionaryConverter(ShaleiaDictionary sourceDictionary) {
    super(sourceDictionary)
  }

  protected BooleanClass convert() {
    List<ShaleiaWord> sourceWords = $sourceDictionary.getRawWords()
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
        ShaleiaDescriptionReader sourceReader = ShaleiaDescriptionReader.new(sourceWord.getDescription())
        try {
          while (sourceReader.readLine() != null) {
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
      updateProgress(i + 1, size)
    }
    return true
  }

  private void addEquivalent(SlimeWord word, String sourcePart, String sourceEquivalent) {
    SlimeEquivalent equivalent = SlimeEquivalent.new()
    equivalent.setTitle(sourcePart)
    equivalent.setNames(sourceEquivalent.replaceAll(/(\{|\}|\/|\(.*?\)\s*)/, "").split(/\s*,\s*/).toList())
    word.getRawEquivalents().add(equivalent)
  }

  private void addContent(SlimeWord word, String sourceTitle, String sourceContent) {
    SlimeInformation information = SlimeInformation.new()
    information.setTitle(sourceTitle)
    information.setText(sourceContent.replaceAll(/(\{|\}|\[|\]|\/)/, ""))
    word.getInformations().add(information)
  }

}