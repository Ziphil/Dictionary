package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Akrantiain {

  public static final String PUNCTUATION_IDENTIIER_NAME = "PUNCTUATION"

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
    AkrantiainElementGroup currentGroup = AkrantiainElementGroup.create(input)
    for (AkrantiainRule rule : $root.getDefaultModule().getRules()) {
      currentGroup = rule.apply(currentGroup, $root.getDefaultModule())
    }
    List<AkrantiainElement> invalidElements = currentGroup.invalidElements($root.getDefaultModule())
    if (invalidElements.isEmpty()) {
      return currentGroup.createOutput()
    } else {
      throw AkrantiainException.new("No rules that can handle some characters", invalidElements)
    }
  }

}