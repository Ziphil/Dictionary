package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.controller.Controller
import ziphil.controller.ShaleiaIndividualSettingController
import ziphil.controller.ShaleiaEditorController
import ziphil.controller.ShaleiaSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditableDictionaryFactory
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Loader
import ziphil.dictionary.Saver
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaDictionaryFactory extends EditableDictionaryFactory {

  private static final String NAME = "シャレイア語辞典形式"
  private static final String EXTENSION = "xdc"
  private static final String ICON_PATH = "resource/image/menu/xdc_dictionary.png"

  protected Dictionary create(File file, Loader loader) {
    if (loader != null) {
      Dictionary dictionary = ShaleiaDictionary.new(file.getName(), file.getPath(), loader)
      return dictionary
    } else {
      Dictionary dictionary = ShaleiaDictionary.new(file.getName(), file.getPath())
      return dictionary
    }
  }

  protected Loader createLoader(File file) {
    ShaleiaLoader loader = ShaleiaLoader.new(file.getPath())
    return loader
  }

  protected Saver createSaver() {
    ShaleiaSaver saver = ShaleiaSaver.new()
    return saver
  }

  public Controller createSearcherController(UtilityStage<SearchParameter> stage, Dictionary dictionary) {
    ShaleiaSearcherController controller = ShaleiaSearcherController.new(stage)
    return controller
  }

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, Dictionary dictionary) {
    ShaleiaIndividualSettingController controller = ShaleiaIndividualSettingController.new(stage)
    controller.prepare((ShaleiaDictionary)dictionary)
    return controller
  }

  public Controller createEditorController(UtilityStage<WordEditResult> stage, Dictionary dictionary, Word word) {
    ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
    controller.prepare((ShaleiaWord)word)
    return controller
  }

  public Controller createCreatorController(UtilityStage<WordEditResult> stage, Dictionary dictionary, Word word) {
    ShaleiaEditorController controller = ShaleiaEditorController.new(stage)
    controller.prepare((ShaleiaWord)word, true)
    return controller
  }

  public Image createIcon() {
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream(ICON_PATH))
    return icon
  }

  public Boolean isCreatable() {
    return false
  }

  public Boolean isSearcherSupported(Dictionary dictionary) {
    return true
  }

  public Boolean isIndividualSettingSupported(Dictionary dictionary) {
    return true
  }

  public String getName() {
    return NAME
  }

  public String getExtension() {
    return EXTENSION
  }

  public Class<? extends Dictionary> getDictionaryClass() {
    return ShaleiaDictionary
  }

}