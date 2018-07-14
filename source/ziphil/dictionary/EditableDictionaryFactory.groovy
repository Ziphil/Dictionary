package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class EditableDictionaryFactory extends DictionaryFactory {

  public abstract Controller createEditorController(UtilityStage<WordEditResult> stage, Dictionary dictionary, Word word)

  public abstract Controller createCreatorController(UtilityStage<WordEditResult> stage, Dictionary dictionary, Word word)

}