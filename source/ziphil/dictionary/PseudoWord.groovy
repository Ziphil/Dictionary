package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PseudoWord {

  private List<String> $equivalents
  private String $content

  public List<String> getEquivalents() {
    return $equivalents
  }

  public void setEquivalents(List<String> equivalents) {
    $equivalents = equivalents
  }

  public String getContent() {
    return $content
  }

  public void setContent(String content) {
    $content = content
  }

}