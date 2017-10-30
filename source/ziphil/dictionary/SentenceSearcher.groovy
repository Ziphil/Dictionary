package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphil.dictionary.shaleia.ShaleiaPossibility
import ziphil.dictionary.shaleia.ShaleiaSuggestion
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimePossibility
import ziphil.dictionary.slime.SlimeSearchParameter
import ziphil.dictionary.slime.SlimeSuggestion
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SentenceSearcher {

  private Dictionary $dictionary
  private String $sentence = ""
  private String $punctuations = ""

  public SentenceSearcher(Dictionary dictionary, String sentence, String punctuations) {
    $dictionary = dictionary
    $sentence = sentence
    $punctuations = punctuations
  }

  public SentenceSearcher(Dictionary dictionary) {
    $dictionary = dictionary
  }

  public ObservableList<Result> search() {
    ObservableList<Result> results = FXCollections.observableArrayList()
    for (String search : $sentence.split(/\s+/)) {
      StringBuilder nextSearch = StringBuilder.new()
      for (String character : search) {
        if ($punctuations.indexOf(character) < 0) {
          nextSearch.append(character)
        }
      }
      if (nextSearch.length() > 0) {
        NormalSearchParameter parameter = NormalSearchParameter.new(nextSearch.toString(), SearchMode.NAME, true, true)
        $dictionary.search(parameter)
        List<Element> hitWords = ArrayList.new($dictionary.getWholeWords())
        Result result = Result.new(nextSearch.toString())
        for (Element word : hitWords) {
          if (word instanceof Word) {
            result.getWords().add((Word)word)
          } else if (word instanceof Suggestion) {
            addSuggestionResult(result, (Suggestion)word)
          }
        }
        results.add(result)
      }
    }
    return results
  }

  private void addSuggestionResult(Result result, Suggestion suggestion) {
    List<Possibility> possibilities = ArrayList.new(suggestion.getPossibilities())
    for (Possibility possibility : possibilities) {
      SearchParameter parameter = possibility.createParameter()
      $dictionary.search(parameter)
      List<Element> hitWords = $dictionary.getWholeWords()
      for (Element word : hitWords) {
        if (word instanceof Word) {
          result.getWords().add((Word)word)
        }
      }
    }
  }

  public void setSentence(String sentence) {
    $sentence = sentence
  }

  public void setPunctuations(String punctuations) {
    $punctuations = punctuations
  }


  @InnerClass @Ziphilify
  public static class Result {

    private String $search = ""
    private List<Word> $words = ArrayList.new()

    public Result(String search) {
      $search = search
    }

    public String getSearch() {
      return $search
    }

    public List<Word> getWords() {
      return $words
    }

  }

}