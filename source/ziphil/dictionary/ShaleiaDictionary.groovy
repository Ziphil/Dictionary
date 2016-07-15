package ziphil.dictionary

import groovy.transform.CompileStatic
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Consumer
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException


@CompileStatic @Newify
public class ShaleiaDictionary extends Dictionary<ShaleiaWord> {

  private Consumer<String> $onLinkClicked

  public ShaleiaDictionary(String name, String path) {
    super(name, path)
    load()
    setupWords()
  }

  public void searchDetail(ShaleiaSearchParameter parameter) {
    String searchName = parameter.getName()
    SearchType type = parameter.getSearchType()
    try {
      Pattern pattern = Pattern.compile(searchName)
      $filteredWords.setPredicate() { ShaleiaWord word ->
        String name = word.getName()
        if (type == SearchType.EXACT) {
          return name = searchName
        } else if (type == SearchType.PREFIX) {
          return name.startsWith(searchName)
        } else if (type == SearchType.SUFFIX) {
          return name.endsWith(searchName)
        } else if (type == SearchType.PART) {
          return name.contains(searchName)
        } else if (type == SearchType.REGULAR_EXPRESSION) {
          Matcher matcher = pattern.matcher(name)
          return matcher.find()
        } else if (type == SearchType.MINIMAL_PAIR) {
          Boolean predicate = false
          (0 ..< searchName.length()).each() { Integer i ->
            String beforeSearchName = (i == 0) ? "" : searchName[0 .. i - 1]
            String afterSearchName = (i == searchName.length() - 1) ? "" : searchName[i + 1 .. -1]
            String searchRegex = "^" + beforeSearchName + "." + afterSearchName + "\$"
            Matcher matcher = Pattern.compile(searchRegex).matcher(name)
            if (matcher.find()) {
              predicate = true
            }
          }
          return predicate
        } else {
          return false
        }
      }
    } catch (PatternSyntaxException exception) {
    }
  }

  public void modifyWord(ShaleiaWord oldWord, ShaleiaWord newWord) {
    newWord.createContentPane()
  }

  public void addWord(ShaleiaWord word) {
    word.setDictionary(this)
    $words.add(word)
  }

  public void removeWord(ShaleiaWord word) {
    $words.remove(word)
  }

  public ShaleiaWord emptyWord() {
    Long hairiaNumber = LocalDateTime.of(2012, 1, 23, 6, 0).until(LocalDateTime.now(), ChronoUnit.DAYS) + 1
    String data = "+ ${hairiaNumber} 〈不〉\n\n=〈〉"
    return ShaleiaWord.new("", data)
  }

  public ShaleiaWord copiedWord(ShaleiaWord oldWord) {
    String name = oldWord.getName()
    String data = oldWord.getData()
    ShaleiaWord newWord = ShaleiaWord.new(name, data)
    return newWord
  }

  public ShaleiaWord inheritedWord(ShaleiaWord oldWord) {
    return copiedWord(oldWord)
  }

  private void load() {
    if ($path != null) {
      File file = File.new($path)
      String currentName = null
      StringBuilder currentData = StringBuilder.new()
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^\*\s*(.+)\s*$/
        if (matcher.matches()) {
          if (currentName != null) {
            ShaleiaWord word = ShaleiaWord.new(currentName, currentData.toString())
            word.setDictionary(this)
            $words.add(word)
          }
          currentName = matcher.group(1)
          currentData.setLength(0)
        } else {
          currentData.append(line)
          currentData.append("\n")
        }
      }
      if (currentName != null) {
        ShaleiaWord word = ShaleiaWord.new(currentName, currentData.toString())
        word.setDictionary(this)
        $words.add(word)
      }
    }
  }

  public void save() {
    File file = File.new($path)
    StringBuilder output = StringBuilder.new()
    $words.each() { ShaleiaWord word ->
      output.append("* " + word.getUniqueName())
      output.append("\n")
      output.append(word.getData().trim())
      output.append("\n\n")
    }
    file.setText(output.toString(), "UTF-8")
  }

  private void setupWords() {
    $sortedWords.setComparator() { ShaleiaWord firstWord, ShaleiaWord secondWord ->
      List<Integer> firstList = firstWord.listForComparison()
      List<Integer> secondList = secondWord.listForComparison()
      Integer result = null
      (0 ..< firstList.size()).each() { Integer i ->
        Integer firstData = firstList[i]
        Integer secondData = secondList[i]
        if (result == null && firstData <=> secondData != 0) {
          result = firstData <=> secondData
        }
      }
      return (result == null) ? -1 : result
    }
  }

  public Consumer<String> getOnLinkClicked() {
    return $onLinkClicked
  }

  public void setOnLinkClicked(Consumer<String> onLinkClicked) {
    $onLinkClicked = onLinkClicked
  }

}