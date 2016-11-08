package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.dictionary.Word
import ziphil.module.Setting


@CompileStatic @Newify
public class ShaleiaWord extends Word {

  private String $uniqueName = ""
  private String $data = ""
  private String $comparisonString = ""
  private ShaleiaDictionary $dictionary

  public ShaleiaWord(String uniqueName, String data) {
    update(uniqueName, data)
  }

  public void update(String uniqueName, String data) {
    $name = uniqueName.replaceAll(/\+|~/, "")
    $uniqueName = uniqueName
    $data = data
    $content = uniqueName + "\n" + data
    BufferedReader reader = BufferedReader.new(StringReader.new(data))
    String line
    while ((line = reader.readLine()) != null) {
      Matcher matcher = line =~ /^\=(?:\:)?\s*(?:〈(.+)〉)?\s*(.+)$/
      if (matcher.matches()) {
        String equivalent = matcher.group(2)
        List<String> equivalents = equivalent.replaceAll(/(\(.+\)|\{|\}|\/|\s)/, "").split(/,/).toList()
        $equivalents.addAll(equivalents)
      }
    }
    reader.close()
    $isChanged = true
  }

  public void createContentPane() {
    Setting setting = Setting.getInstance()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    ShaleiaWordContentPaneCreator creator = ShaleiaWordContentPaneCreator.new($contentPane, this, $dictionary)
    creator.setModifiesPunctuation(modifiesPunctuation)
    creator.create()
    $isChanged = false
  }

  public void createComparisonString(String order) {
    StringBuilder comparisonString = StringBuilder.new()
    for (Integer i : 0 ..< $name.length()) {
      if ($name[i] != "'") {
        Integer position = order.indexOf($name.codePointAt(i))
        if (position > -1) {
          comparisonString.appendCodePoint(position + 174)
        } else {
          comparisonString.appendCodePoint(10000)
        }
      }
    }
    $comparisonString = comparisonString.toString()
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

  public String getUniqueName() {
    return $uniqueName
  }

  public String getData() {
    return $data
  }

  public String getComparisonString() {
    return $comparisonString
  }

}