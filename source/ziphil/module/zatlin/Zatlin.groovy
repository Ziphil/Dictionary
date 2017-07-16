package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphil.module.NameGenerator
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Zatlin implements NameGenerator {

  private ZatlinRoot $root

  public void load(File file) {
    Reader reader = InputStreamReader.new(FileInputStream.new(file), "UTF-8")
    parse(reader)
  }

  public void load(String source) {
    Reader reader = StringReader.new(source)
    parse(reader)
  }

  private void parse(Reader reader) {
    ZatlinParser parser = ZatlinParser.new(reader)
    $root = parser.readRoot()
  }

  public String generate() {
    return $root.generate()
  }

}