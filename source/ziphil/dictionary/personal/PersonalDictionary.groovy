package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Suggestion


@CompileStatic @Newify
public class PersonalDictionary extends Dictionary<PersonalWord, Suggestion> {

  private PersonalDictionaryLoader $loader

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

  public PersonalWord emptyWord() {
    return PersonalWord.new("", "", "", "", 0, 0, 0)
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

  private void load() {
    $loader = PersonalDictionaryLoader.new($path)
    $loader.addEventFilter(WorkerStateEvent.WORKER_STATE_SUCCEEDED) { WorkerStateEvent event ->
      $words.addAll($loader.getValue())
    }
    Thread thread = Thread.new(loader)
    thread.setDaemon(true)
    thread.start()
  }

  public void save() {
    if ($path != null) {
      File file = File.new($path)
      StringBuilder output = StringBuilder.new()
      output.append("word,trans,exp,level,memory,modify,pron,filelink\n")
      $words.each() { PersonalWord word ->
        output.append("\"").append(word.getName()).append("\",")
        output.append("\"").append(word.getTranslation()).append("\",")
        output.append("\"").append(word.getUsage()).append("\",")
        output.append(word.getLevel().toString()).append(",")
        output.append(word.getMemory().toString()).append(",")
        output.append(word.getModification().toString()).append(",")
        output.append("\"").append(word.getPronunciation()).append("\"\n")
      }
      file.setText(output.toString(), "UTF-8")
    }
    $isChanged = false
  }

  private void setupWords() {
    $sortedWords.setComparator() { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

  public Task<?> getLoader() {
    return $loader
  }

}