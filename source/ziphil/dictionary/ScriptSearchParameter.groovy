package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ScriptSearchParameter extends SearchParameter {

  private String $script = ""

  public ScriptSearchParameter(String script) {
    $script = script
  }

  public String getScript() {
    return $script
  }

  public void setScript(String script) {
    $script = script
  }

}