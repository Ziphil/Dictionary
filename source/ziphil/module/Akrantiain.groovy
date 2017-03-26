package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  private EnumSet<AkrantiainEnvironment> $environments = EnumSet.noneOf(AkrantiainEnvironment)
  private List<AkrantiainRule> $rules = ArrayList.new()

  public void load(File file) {
    BufferedReader reader = file.newReader("UTF-8")
    parse(reader)
  }

  public void load(String string) {
    BufferedReader reader = BufferedReader.new(StringReader.new(string))
    parse(reader)
  }

  private void parse(BufferedReader reader) {
  }

  public String convert(String input) {
    return input
  }

}