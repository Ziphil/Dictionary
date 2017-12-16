package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.custom.UtilityStage
import ziphil.dictionary.Word
import ziphil.module.TemporarySetting


@CompileStatic
public interface EditorControllerSupplier {

  public Controller getEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting)

  public Controller getCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting)
  
}