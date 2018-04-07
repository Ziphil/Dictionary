package ziphil.custom

import groovy.transform.CompileStatic
import java.util.function.Consumer
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.model.RichTextChange
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum RichTextLanguage {

  SHALEIA_DICTIONARY,
  AKRANTIAIN,
  ZATLIN

  public Consumer<RichTextChange> createConsumer(CodeArea codeArea) {
    RichTextChangeConsumer consumer = RichTextChangeConsumer.new(codeArea)
    if (this == SHALEIA_DICTIONARY) {
      consumer.addSyntax(/(?m)^(\+)(\s*\d+)?(\s*〈.*〉)?/, "shaleia-creation-date-marker", "shaleia-creation-date", "shaleia-total-part")
      consumer.addSyntax(/(?m)^([A-Z]>)/, "shaleia-content-marker")
      consumer.addSyntax(/(?m)^(\=:?)(\s*〈.*〉)?/, "shaleia-equivalent-marker", "shaleia-part")
      consumer.addSyntax(/(?m)^(\-)(\s*〈.*〉)?/, "shaleia-synonym-marker", "shaleia-part")
      consumer.addSyntax(/(\{|\}|\[|\]|\/)(\*)?/, "shaleia-symbol", "shaleia-reference-mark")
    } else if (this == AKRANTIAIN) {
      consumer.addSyntax(/(?m)(#.*$)/, "akrantiain-comment")
      consumer.addSyntax(/(?m)(\".*?(?:(?<!\\)\"|$)|\/.*?(?:(?<!\\)\/|$))/, "akrantiain-string")
      consumer.addSyntax(/(\@[A-Za-z0-9_]+)/, "akrantiain-environment")
      consumer.addSyntax(/([A-Za-z0-9_]+)/, "akrantiain-identifier")
      consumer.addSyntax(/(\^|\$)/, "akrantiain-special-symbol")
      consumer.addSyntax(/(%%|%)/, "akrantiain-marker")
      consumer.addSyntax(/(;)/, "akrantiain-semicolon")
    } else if (this == ZATLIN) {
      consumer.addSyntax(/(?m)(#.*$)/, "zatlin-comment")
      consumer.addSyntax(/(?m)(\".*?(?:(?<!\\)\"|$))/, "zatlin-string")
      consumer.addSyntax(/([A-Za-z_][A-Za-z0-9_]*)/, "zatlin-identifier")
      consumer.addSyntax(/([0-9]+|[0-9]*\.[0-9]*)/, "zatlin-numeric")
      consumer.addSyntax(/(\^)/, "zatlin-special-symbol")
      consumer.addSyntax(/(%)/, "zatlin-marker")
      consumer.addSyntax(/(;)/, "zatlin-semicolon")
    }
    return consumer
  }

}