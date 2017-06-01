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

  public ObservableList<List<Word>> search() {
    ObservableList<List<Word>> results = FXCollections.observableArrayList()
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
        List<Word> result = ArrayList.new()
        for (Element word : hitWords) {
          if (word instanceof Word) {
            result.add((Word)word)
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

  private void addShaleiaSuggestionResult(List<Word> result, ShaleiaSuggestion suggestion) {
    ShaleiaDictionary dictionary = (ShaleiaDictionary)$dictionary
    List<ShaleiaPossibility> possibilities = ArrayList.new(suggestion.getPossibilities())
    for (ShaleiaPossibility possibility : possibilities) {
      NormalSearchParameter parameter = NormalSearchParameter.new(possibility.getName(), SearchMode.NAME, true, true)
      dictionary.searchNormal(parameter)
      List<Element> hitWords = dictionary.getWholeWords()
      for (Element word : hitWords) {
        if (word instanceof Word) {
          result.add((Word)word)
        }
      }
    }
  }

  private void addSlimeSuggestionResult(List<Word> result, SlimeSuggestion suggestion) {
    SlimeDictionary dictionary = (SlimeDictionary)$dictionary
    List<SlimePossibility> possibilities = ArrayList.new(suggestion.getPossibilities())
    for (SlimePossibility possibility : possibilities) {
      SlimeSearchParameter parameter = SlimeSearchParameter.new()
      parameter.setId(possibility.getWord().getId())
      dictionary.searchDetail(parameter)
      List<Element> hitWords = dictionary.getWholeWords()
      for (Element word : hitWords) {
        if (word instanceof Word) {
          result.add((Word)word)
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