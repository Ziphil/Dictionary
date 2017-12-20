package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.controller.Controller
import ziphil.controller.PersonalEditorController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditorControllerFactory
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.module.TemporarySetting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalEditorControllerFactory implements EditorControllerFactory {

  private PersonalDictionary $dictionary

  public PersonalEditorControllerFactory(PersonalDictionary dictionary) {
    $dictionary = dictionary
  }

  public Controller createEditorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    PersonalEditorController controller = PersonalEditorController.new(stage)
    controller.prepare((PersonalWord)word)
    return controller
  }

  public Controller createCreatorController(UtilityStage<WordEditResult> stage, Word word, TemporarySetting temporarySetting) {
    PersonalEditorController controller = PersonalEditorController.new(stage)
    controller.prepare((PersonalWord)word, true)
    return controller
  }

}