package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.ShaleiaEditorController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditorControllerSupplier
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.module.TemporarySetting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaEditorControllerSupplier implements EditorControllerSupplier {

  private ShaleiaDictionary $dictionary

  public ShaleiaEditorControllerSupplier(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller getEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
    controller.prepare((ShaleiaWord)word)
    return controller
  }

  public Controller getCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
    controller.prepare((ShaleiaWord)word, true)
    return controller
  }

}