package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  private AkrantiainRoot $root

  public void load(File file) {
    Reader reader = InputStreamReader.new(FileInputStream.new(file), "UTF-8")
    parse(reader)
  }

  public void load(String source) {
    Reader reader = StringReader.new(source)
    parse(reader)
  }

  private void parse(Reader reader) {
    AkrantiainParser parser = AkrantiainParser.new(reader)
    $root = parser.readRoot()
  }

  public String convert(String input) {
    return $root.convert(input)
  }

}