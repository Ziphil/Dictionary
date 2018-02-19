package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphil.dictionary.Word
import ziphil.module.TemporarySetting


@CompileStatic
public interface EditorControllerFactory {

  public Controller createEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting)

  public Controller createCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting)
  
}