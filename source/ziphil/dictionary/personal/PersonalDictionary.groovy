package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphil.dictionary.ConjugationResolver
import ziphil.dictionary.Dictionary
import ziphil.dictionary.EditableDictionaryBase
import ziphil.dictionary.EmptyConjugationResolver
import ziphil.dictionary.ExportConfig
import ziphil.dictionary.Loader
import ziphil.dictionary.NormalSearchParameter
import ziphil.dictionary.PseudoWord
import ziphil.dictionary.Saver
import ziphil.dictionary.Suggestion
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionary extends EditableDictionaryBase<PersonalWord, Suggestion, PersonalDictionaryFactory> {

  public PersonalDictionary(String name, String path) {
    super(name, path)
  }

  public PersonalDictionary(String name, String path, Loader loader) {
    super(name, path, loader)
  }

  protected void prepare() {
  }

  public PersonalWord createWord(String defaultName) {
    PersonalWord word = PersonalWord.new()
    word.setName(defaultName ?: "")
    word.update()
    return word
  }

  public PersonalWord copyWord(PersonalWord oldWord) {
    PersonalWord newWord = PersonalWord.new()
    newWord.setName(oldWord.getName())
    newWord.setPronunciation(oldWord.getPronunciation())
    newWord.setTranslation(oldWord.getTranslation())
    newWord.setUsage(oldWord.getUsage())
    newWord.setLevel(oldWord.getLevel())
    newWord.setMemory(oldWord.getMemory())
    newWord.setModification(oldWord.getModification())
    newWord.setDictionary(this)
    newWord.update()
    return newWord
  }

  public PersonalWord inheritWord(PersonalWord oldWord) {
    return copyWord(oldWord)
  }

  public PersonalWord determineWord(String name, PseudoWord pseudoWord) {
    PersonalWord word = PersonalWord.new()
    List<String> pseudoEquivalents = pseudoWord.getEquivalents()
    String pseudoContent = pseudoWord.getContent()
    word.setName(name)
    word.setTranslation(pseudoEquivalents.join(", "))
    if (pseudoContent != null) {
      word.setUsage(pseudoContent)
    }
    word.setDictionary(this)
    word.update()
    return word
  }

  public Object createPlainWord(PersonalWord oldWord) {
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

  public PersonalDictionary copy() {
    PersonalDictionary dictionary = PersonalDictionary.new($name, null)
    dictionary.setPath($path)
    dictionary.getRawWords().addAll($words)
    return dictionary
  }

  public Boolean containsName(String name, PersonalWord excludedWord) {
    return $words.any{it != excludedWord && it.getName() == name}
  }

  protected Comparator<? super PersonalWord> createWordComparator() {
    Comparator<PersonalWord> comparator = { PersonalWord firstWord, PersonalWord secondWord ->
      return firstWord.getName() <=> secondWord.getName()
    }
    return comparator
  }

  protected ConjugationResolver createConjugationResolver() {
    EmptyConjugationResolver conjugationResolver = EmptyConjugationResolver.new($suggestions)
    return conjugationResolver
  }

}