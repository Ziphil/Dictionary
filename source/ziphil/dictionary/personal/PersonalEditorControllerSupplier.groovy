package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.PersonalEditorController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditorControllerSupplier
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.module.TemporarySetting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalEditorControllerSupplier implements EditorControllerSupplier {

  private PersonalDictionary $dictionary

  public PersonalEditorControllerSupplier(PersonalDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller getEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    PersonalEditorController controller = PersonalEditorController.new(stage)
    controller.prepare((PersonalWord)word)
    return controller
  }

  public Controller getCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    PersonalEditorController controller = PersonalEditorController.new(stage)
    controller.prepare((PersonalWord)word, true)
    return controller
  }


}