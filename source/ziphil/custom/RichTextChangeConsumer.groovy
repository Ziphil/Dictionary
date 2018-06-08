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
        List<IntegerClass> matchedGroups = ArrayList.new()
        for (int i = 1 ; i <= matcher.groupCount() ; i ++) {
          if (matcher.group(i) != null) {
            matchedGroups.add(i)
          }
        }
        builder.add([], matcher.start() - index)
        for (int i = 0 ; i < matchedGroups.size() ; i ++) {
          Int group = matchedGroups[i]
          if (i == 0) {
            builder.add([], matcher.start(group) - matcher.start())
          } else {
            Int previousGroup = matchedGroups[i - 1]
            builder.add([], matcher.start(group) - matcher.end(previousGroup))
          }
          builder.add([matchedType.getNames()[group - 1]], matcher.end(group) - matcher.start(group))
        }
        if (!matchedGroups.isEmpty()) {
          builder.add([], matcher.end() - matcher.end(matchedGroups.last()))
        } else {
          builder.add([], matcher.end() - matcher.start())
        }
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