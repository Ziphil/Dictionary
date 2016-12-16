package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Suggestion
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionary extends Dictionary<PersonalWord, Suggestion> {

  public PersonalDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
  }

  public void modifyWord(PersonalWord oldWord, PersonalWord newWord) {
    newWord.updateContentPane()
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

  private void setupWords() {
    $sortedWords.setComparator() { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

  protected Task<?> createLoader() {
    PersonalDictionaryLoader loader = PersonalDictionaryLoader.new(this, $path)
    return loader
  }

  protected Task<?> createSaver() {
    PersonalDictionarySaver saver = PersonalDictionarySaver.new(this, $path)
    return saver
  }

}