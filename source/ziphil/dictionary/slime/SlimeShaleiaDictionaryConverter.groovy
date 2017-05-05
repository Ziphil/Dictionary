package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.DictionaryConverter
import ziphil.dictionary.Word
import ziphil.dictionary.shaleia.ShaleiaDescriptionReader
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaWord
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeShaleiaDictionaryConverter extends DictionaryConverter<SlimeDictionary, ShaleiaDictionary, SlimeWord> {

  public SlimeShaleiaDictionaryConverter(SlimeDictionary newDictionary, ShaleiaDictionary oldDictionary) {
    super(newDictionary, oldDictionary)
  }

  protected Boolean convert() {
    List<ShaleiaWord> oldWords = $oldDictionary.getRawWords()
    Integer size = oldWords.size()
    for (Integer i : 0 ..< size) {
      if (isCancelled()) {
        return false
      }
      ShaleiaWord oldWord = oldWords[i]
      if (!oldWord.getName().startsWith("\$")) {
        SlimeWord newWord = SlimeWord.new()
        newWord.setId(i + 1)
        newWord.setName(oldWord.getName())
        ShaleiaDescriptionReader oldReader = ShaleiaDescriptionReader.new(oldWord.getDescription())
        try {
          while (oldReader.readLine() != null) {
            if (oldReader.findEquivalent()) {
              String part = oldReader.lookupPart()
              String equivalent = oldReader.lookupEquivalent()
              addEquivalent(newWord, part, equivalent)
            }
            if (oldReader.findContent()) {
              String title = oldReader.title()
              String content = oldReader.lookupContent()
              addContent(newWord, title, content)
            }
          }
          newWord.setDictionary($newDictionary)
          $newWords.add(newWord)
        } finally {
          oldReader.close()
        }
      }
      updateProgress(i + 1, size)
    }
    return true
  }

  private void addEquivalent(SlimeWord newWord, String part, String equivalent) {
    SlimeEquivalent newEquivalent = SlimeEquivalent.new()
    newEquivalent.setTitle(part)
    newEquivalent.setNames(equivalent.replaceAll(/(\{|\}|\/|\(.*?\)\s*)/, "").split(/\s*,\s*/).toList())
    newWord.getRawEquivalents().add(newEquivalent)
  }

  private void addContent(SlimeWord newWord, String title, String content) {
    SlimeInformation newInformation = SlimeInformation.new()
    newInformation.setTitle(title)
    newInformation.setText(content.replaceAll(/(\{|\}|\[|\]|\/)/, ""))
    newWord.getInformations().add(newInformation)
  }

}