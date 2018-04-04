package ziphil.custom

import groovy.transform.CompileStatic
import java.util.function.Consumer
import java.util.regex.Matcher
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.model.RichTextChange
import org.fxmisc.richtext.model.StyleSpansBuilder
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class RichTextChangeConsumer implements Consumer<RichTextChange> {

  private CodeArea $codeArea
  private List<SyntaxType> $types = ArrayList.new()

  public RichTextChangeConsumer(CodeArea codeArea) {
    $codeArea = codeArea
  }

  public void accept(RichTextChange change) {
    StyleSpansBuilder<Collection<String>> builder = StyleSpansBuilder.new()
    String text = $codeArea.textProperty().getValue()
    for (SyntaxType type : $types) {
      type.updateMatcher(text)
    }
    Int index = 0
    while (true) {
      SyntaxType matchedType = null
      Int startIndex = text.length()
      for (SyntaxType type : $types) {
        Matcher matcher = type.getMatcher()
        if (matcher.find(index)) {
          if (matcher.start() < startIndex) {
            startIndex = matcher.start()
            matchedType = type
          }
        }
      }
      if (matchedType != null) {
        Matcher matcher = matchedType.getMatcher()
        builder.add([], matcher.start() - index)
        for (int i = 1 ; i <= matcher.groupCount() ; i ++) {
          if (i == 1) {
            builder.add([], matcher.start(i) - matcher.start())
          } else {
            builder.add([], matcher.start(i) - matcher.end(i - 1))
          }
          builder.add([matchedType.getNames()[i - 1]], matcher.end(i) - matcher.start(i))
        }
        builder.add([], matcher.end() - matcher.end(matcher.groupCount()))
        index = matcher.end()
      } else {
        builder.add([], text.length() - index)
        break
      }
    }
    $codeArea.setStyleSpans(0, builder.create())
  }

  public void addSyntax(String regex, String... names) {
    SyntaxType type = SyntaxType.new(regex, names)
    $types.add(type)
  }

}


@InnerClass(RichTextChangeConsumer)
@CompileStatic @Ziphilify
private static class SyntaxType {

  private String[] $names
  private String $regex
  private Matcher $matcher

  public SyntaxType(String regex, String... names) {
    $names = names
    $regex = regex
  }

  public void updateMatcher(String text) {
    $matcher = text =~ $regex
  }

  public String[] getNames() {
    return $names
  }

  public Matcher getMatcher() {
    return $matcher
  }

}