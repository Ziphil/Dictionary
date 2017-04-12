package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitiveArgs
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainModuleName {

  private List<AkrantiainToken> $tokens = ArrayList.new()

  public List<AkrantiainToken> getTokens() {
    return $tokens
  }

  public void setTokens(List<AkrantiainToken> tokens) {
    $tokens = tokens
  }

}