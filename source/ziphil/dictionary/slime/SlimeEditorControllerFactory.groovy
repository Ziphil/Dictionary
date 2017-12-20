package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.SlimeEditorController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditorControllerFactory
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.module.TemporarySetting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeEditorControllerFactory implements EditorControllerFactory {

  private SlimeDictionary $dictionary

  public SlimeEditorControllerFactory(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller createEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    SlimeEditorController controller = SlimeEditorController.new(stage)
    controller.prepare((SlimeWord)word, $dictionary, temporarySetting)
    return controller
  }

  public Controller createCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    SlimeEditorController controller = SlimeEditorController.new(stage)
    controller.prepare((SlimeWord)word, $dictionary, temporarySetting, true)
    return controller
  }

}