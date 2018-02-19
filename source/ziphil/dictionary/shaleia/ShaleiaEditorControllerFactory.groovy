package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.ShaleiaEditorController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditorControllerFactory
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.module.TemporarySetting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaEditorControllerFactory implements EditorControllerFactory {

  private ShaleiaDictionary $dictionary

  public ShaleiaEditorControllerFactory(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller createEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
    controller.prepare((ShaleiaWord)word)
    return controller
  }

  public Controller createCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
    controller.prepare((ShaleiaWord)word, true)
    return controller
  }

}