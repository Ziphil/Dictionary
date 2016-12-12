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
    newWord.createContentPane()
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

  public PersonalWord emptyWord(String defaultName) {
    if (defaultName != null) {
      return PersonalWord.new(defaultName, "", "", "", 0, 0, 0)
    } else {
      return PersonalWord.new("", "", "", "", 0, 0, 0)
    }
  }

  public PersonalWord copiedWord(PersonalWord oldWord) {
    String name = oldWord.getName()
    String pronunciation = oldWord.getPronunciation()
    String translation = oldWord.getTranslation()
    String usage = oldWord.getUsage()
    Integer level = oldWord.getLevel()
    Integer memory = oldWord.getMemory()
    Integer modification = oldWord.getModification()
    PersonalWord newWord = PersonalWord.new(name, pronunciation, translation, usage, level, memory, modification)
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