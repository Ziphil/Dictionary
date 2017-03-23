package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphil.dictionary.EditableDictionary
import ziphil.dictionary.DetailSearchParameter
import ziphil.dictionary.DictionaryBase
import ziphil.dictionary.DictionarySaver
import ziphil.dictionary.Suggestion
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionary extends DictionaryBase<PersonalWord, Suggestion> implements EditableDictionary<PersonalWord, PersonalWord> {

  public PersonalDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
  }

  public void modifyWord(PersonalWord oldWord, PersonalWord newWord) {
    $isChanged = true
  }

  public void addWord(PersonalWord word) {
    $words.add(word)
    $isChanged = true
  }

  public void removeWord(PersonalWord word) {
    $words.remove(word)
    $isChanged = true
  }

  public void update() {
    $isChanged = true
  }

  public void updateMinimum() {
    $isChanged = true
  }

  public PersonalWord emptyWord(String defaultName) {
    PersonalWord word = PersonalWord.new()
    word.setName(defaultName ?: "")
    word.update()
    return word
  }

  public PersonalWord copiedWord(PersonalWord oldWord) {
    PersonalWord newWord = PersonalWord.new()
    newWord.setName(oldWord.getName())
    newWord.setPronunciation(oldWord.getPronunciation())
    newWord.setTranslation(oldWord.getTranslation())
    newWord.setUsage(oldWord.getUsage())
    newWord.setLevel(oldWord.getLevel())
    newWord.setMemory(oldWord.getMemory())
    newWord.setModification(oldWord.getModification())
    newWord.update()
    return newWord
  }

  public PersonalWord inheritedWord(PersonalWord oldWord) {
    return copiedWord(oldWord)
  }

  public Object plainWord(PersonalWord oldWord) {
    PersonalPlainWord newWord = PersonalPlainWord.new()
    newWord.setName(oldWord.getName())
    newWord.setPronunciation(oldWord.getPronunciation())
    newWord.setTranslation(oldWord.getTranslation())
    newWord.setUsage(oldWord.getUsage())
    newWord.setLevel(oldWord.getLevel())
    newWord.setMemory(oldWord.getMemory())
    newWord.setModification(oldWord.getModification())
    return newWord
  }

  private void setupWords() {
    $sortedWords.setComparator() { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

  protected Task<?> createLoader() {
    PersonalDictionaryLoader loader = PersonalDictionaryLoader.new(this, $path)
    return loader
  }

  protected DictionarySaver createSaver() {
    PersonalDictionarySaver saver = PersonalDictionarySaver.new(this, $path)
    return saver
  }

}