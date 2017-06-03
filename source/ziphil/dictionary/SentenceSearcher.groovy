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
    for (String wordName : $sentence.split(/\s+/)) {
      StringBuilder nextWordName = StringBuilder.new()
      for (String character : wordName) {
        if ($punctuations.indexOf(character) < 0) {
          nextWordName.append(character)
        }
      }
      if (nextWordName.length() > 0) {
        NormalSearchParameter parameter = NormalSearchParameter.new(nextWordName.toString(), SearchMode.NAME, true, true)
        $dictionary.searchNormal(parameter)
        List<Element> hitWords = ArrayList.new($dictionary.getWholeWords())
        Result result = Result.new(nextWordName.toString())
        for (Element word : hitWords) {
          if (word instanceof Word) {
            result.getWords().add((Word)word)
          } else {
            if ($dictionary instanceof ShaleiaDictionary && word instanceof ShaleiaSuggestion) {
              addShaleiaSuggestionResult(result, (ShaleiaSuggestion)word)
            } else if ($dictionary instanceof SlimeDictionary && word instanceof SlimeSuggestion) {
              addSlimeSuggestionResult(result, (SlimeSuggestion)word)
            }
          }
        }
        results.add(result)
      }
    }
    return results
  }

  private void addShaleiaSuggestionResult(Result result, ShaleiaSuggestion suggestion) {
    ShaleiaDictionary dictionary = (ShaleiaDictionary)$dictionary
    List<ShaleiaPossibility> possibilities = ArrayList.new(suggestion.getPossibilities())
    for (ShaleiaPossibility possibility : possibilities) {
      NormalSearchParameter parameter = NormalSearchParameter.new(possibility.getName(), SearchMode.NAME, true, true)
      dictionary.searchNormal(parameter)
      List<Element> hitWords = dictionary.getWholeWords()
      for (Element word : hitWords) {
        if (word instanceof Word) {
          result.getWords().add((Word)word)
        }
      }
    }
  }

  private void addSlimeSuggestionResult(Result result, SlimeSuggestion suggestion) {
    SlimeDictionary dictionary = (SlimeDictionary)$dictionary
    List<SlimePossibility> possibilities = ArrayList.new(suggestion.getPossibilities())
    for (SlimePossibility possibility : possibilities) {
      SlimeSearchParameter parameter = SlimeSearchParameter.new(possibility.getWord().getId())
      dictionary.searchDetail(parameter)
      List<Element> hitWords = dictionary.getWholeWords()
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

}


@InnerClass(SentenceSearcher)
@Ziphilify
public static class Result {

  private String $name = ""
  private List<Word> $words = ArrayList.new()

  public Result(String name) {
    $name = name
  }

  public String getName() {
    return $name
  }

  public List<Word> getWords() {
    return $words
  }

}