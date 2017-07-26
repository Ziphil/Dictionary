package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  private AkrantiainRoot $root

  public void load(File file) {
    Reader reader = InputStreamReader.new(FileInputStream.new(file), "UTF-8")
    AkrantiainParser parser = AkrantiainParser.new(reader)
    $root = parser.readRoot()
  }

  public void load(String source) {
    Reader reader = StringReader.new(source)
    AkrantiainParser parser = AkrantiainParser.new(reader)
    $root = parser.readRoot()
  }

  public String convert(String input) {
    return $root.convert(input)
  }

  public List<AkrantiainWarning> getWarnings() {
    return $root.getWarnings()
  }

}