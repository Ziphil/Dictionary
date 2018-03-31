package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.SkinBase
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CodeAreaWrapperSkin extends SkinBase<CodeAreaWrapper> {

  private CodeAreaWrapper $control

  public CodeAreaWrapperSkin(CodeAreaWrapper control) {
    super(control)
    $control = control
    setupNode()
  }

  private void setupNode() {
    CodeArea codeArea = $control.getCodeArea()
    codeArea.setWrapText(true)
    VirtualizedScrollPane pane = VirtualizedScrollPane.new(codeArea)
    getChildren().add(pane)
  }

}