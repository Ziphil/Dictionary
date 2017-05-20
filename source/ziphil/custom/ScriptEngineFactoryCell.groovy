package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javax.script.ScriptEngineFactory
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean


@CompileStatic @Ziphilify
public class ScriptEngineFactoryCell extends ListCell<ScriptEngineFactory> {

  public ScriptEngineFactoryCell() {
    super()
  }

  protected void updateItem(ScriptEngineFactory factory, PrimBoolean isEmpty) {
    super.updateItem(factory, isEmpty)
    if (isEmpty || factory == null) {
      setText(null)
      setGraphic(null)
    } else {
      String name = factory.getLanguageName()
      String version = factory.getLanguageVersion()
      setText("${name} (${version})")
      setGraphic(null)
    }
  }

}