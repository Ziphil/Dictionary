package ziphil.dictionary

import groovy.transform.CompileStatic
import java.util.regex.Matcher


@CompileStatic @Newify
public class PersonalDictionary extends Dictionary<PersonalWord, Suggestion> {

  public PersonalDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
  }

  public void modifyWord(PersonalWord oldWord, PersonalWord newWord) {
    newWord.createContentPane()
  }

  public void addWord(PersonalWord word) {
    $words.add(word)
  }

  public void removeWord(PersonalWord word) {
    $words.remove(word)
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
    if ($path != null) {
      File file = File.new($path)
      String input = file.getText()
      Matcher matcher = input =~ /(?s)"(.*?)","(.*?)","(.*?)",(\d*?),(\d*?),(\d*?),"(.*?)"/
      matcher.each() { List<String> matches ->
        PersonalWord word = PersonalWord.new(matches[1], matches[7], matches[2], matches[3], matches[4].toInteger(), matches[5].toInteger(), matches[6].toInteger())
        $words.add(word)
      }
    }
  }

  public void save() {
    File file = File.new($path)
    StringBuilder output = StringBuilder.new()
    output.append("word,trans,exp,level,memory,modify,pron,filelink\n")
    $words.each() { PersonalWord word ->
      output.append("\"" + word.getName() + "\",")
      output.append("\"" + word.getTranslation() + "\",")
      output.append("\"" + word.getUsage() + "\",")
      output.append(word.getLevel().toString() + ",")
      output.append(word.getMemory().toString() + ",")
      output.append(word.getModification().toString() + ",")
      output.append("\"" + word.getPronunciation() + "\"\n")
    }
    file.setText(output.toString(), "UTF-8")
  }

  private void setupWords() {
    $sortedWords.setComparator() { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
  }

}