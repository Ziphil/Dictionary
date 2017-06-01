package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import java.util.regex.Pattern
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDescriptionReader implements Closeable, AutoCloseable {

  private Reader $reader
  private String $line = null
  private Matcher $matcher = null
  private DescriptionType $type = null

  public ShaleiaDescriptionReader(String description) {
    $reader = BufferedReader.new(StringReader.new(description))
  }

  public String readLine() {
    $line = $reader.readLine()
    $matcher = null
    $type = null
    return $line
  }

  private Boolean find(DescriptionType type) {
    $matcher = type.compilePattern().matcher($line)
    if ($matcher.find()) {
      $type = type
      return true
    } else {
      $type = null
      return false
    }
  }

  public Boolean findCreationDate() {
    return find(DescriptionType.CREATION_DATE)
  }

  public Boolean findEquivalent() {
    return find(DescriptionType.EQUIVALENT)
  }

  public Boolean findHiddenEquivalent() {
    return find(DescriptionType.HIDDEN_EQUIVALENT)
  }

  public Boolean findContent() {
    return find(DescriptionType.CONTENT)
  }

  public Boolean findSynonym() {
    return find(DescriptionType.SYNONYM)
  }

  private String lookup(DescriptionType type, Integer group) {
    if ($matcher != null && $type == type) {
      return $matcher.group(group)
    } else {
      return null
    }
  }

  public String lookupTotalPart() {
    return lookup(DescriptionType.CREATION_DATE, 2)
  }

  public String lookupCreationDate() {
    return lookup(DescriptionType.CREATION_DATE, 1)
  }

  public String lookupPart() {
    return lookup(DescriptionType.EQUIVALENT, 1)
  }

  public String lookupEquivalent() {
    String equivalent = lookup(DescriptionType.EQUIVALENT, 2) 
    if (equivalent != null) {
      return equivalent
    } else {
      return lookup(DescriptionType.HIDDEN_EQUIVALENT, 1)
    }
  }

  public String lookupContent() {
    return lookup(DescriptionType.CONTENT, 2)
  }

  public String lookupSynonym() {
    return lookup(DescriptionType.SYNONYM, 1)
  }

  public String title() {
    if ($matcher != null && $type == DescriptionType.CONTENT) {
      String alphabet = $matcher.group(1)
      if (alphabet == "M") {
        return "語義"
      } else if (alphabet == "E") {
        return "語源"
      } else if (alphabet == "U") {
        return "語法"
      } else if (alphabet == "P") {
        return "成句"
      } else if (alphabet == "N") {
        return "備考"
      } else if (alphabet == "O") {
        return "タスク"
      } else if (alphabet == "S") {
        return "例文"
      } else {
        return ""
      }
    } else {
      return null
    }
  }

  public void close() {
    $reader.close()
  }

}


@InnerClass(ShaleiaDescriptionReader)
private static enum DescriptionType {

  CREATION_DATE(/^\+\s*(\d+)(?:\s*〈(.+)〉)?\s*$/),
  EQUIVALENT(/^\=\s*〈(.+)〉\s*(.+)$/),
  HIDDEN_EQUIVALENT(/^\=:\s*(.+)$/),
  CONTENT(/^([A-Z])>\s*(.+)$/),
  SYNONYM(/^\-\s*(.+)$/)

  private String $regex

  private DescriptionType(String regex) {
    $regex = regex
  }

  public Pattern compilePattern() {
    Pattern pattern = Pattern.compile($regex)
    return pattern
  }

}