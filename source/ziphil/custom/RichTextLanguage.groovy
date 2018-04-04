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
      consumer.addSyntax(/(?m)^(\+)\s*(\d+)(\s*〈.*〉|)/, "shaleia-creation-date-marker", "shaleia-creation-date", "shaleia-total-part")
      consumer.addSyntax(/(?m)^([A-Z]>)/, "shaleia-content-marker")
      consumer.addSyntax(/(?m)^(\=:?)(\s*〈.*〉|)/, "shaleia-equivalent-marker", "shaleia-part")
      consumer.addSyntax(/(?m)^(\-)(\s*〈.*〉|)/, "shaleia-synonym-marker", "shaleia-part")
      consumer.addSyntax(/(\{|\}|\[|\]|\/)(\*|)/, "shaleia-symbol", "shaleia-reference-mark")
    }
    return consumer
  }

}