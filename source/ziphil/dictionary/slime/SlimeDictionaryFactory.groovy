package ziphil.dictionary.slime

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import javafx.scene.image.Image
import ziphil.controller.Controller
import ziphil.controller.SlimeIndividualSettingController
import ziphil.controller.SlimeEditorController
import ziphil.controller.SlimeSearcherController
import ziphil.custom.UtilityStage
import ziphil.dictionary.EditableDictionaryFactory
import ziphil.dictionary.Dictionary
import ziphil.dictionary.Loader
import ziphil.dictionary.Saver
import ziphil.dictionary.SearchParameter
import ziphil.dictionary.Word
import ziphil.dictionary.WordEditResult
import ziphil.dictionary.personal.PersonalDictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeDictionaryFactory extends EditableDictionaryFactory {

  private static final String NAME = "OneToMany-JSON形式"
  private static final String EXTENSION = "json"
  private static final String ICON_PATH = "resource/image/menu/otm_dictionary.png"

  private static ObjectMapper $$mapper = createObjectMapper()

  protected Dictionary create(File file, Loader loader) {
    if (loader != null) {
      Dictionary dictionary = SlimeDictionary.new(file.getName(), file.getPath(), loader)
      return dictionary
    } else {
      Dictionary dictionary = SlimeDictionary.new(file.getName(), file.getPath())
      return dictionary
    }
  }

  protected Loader createLoader(File file) {
    SlimeLoader loader = SlimeLoader.new(file.getPath())
    loader.setMapper($$mapper)
    return loader
  }

  protected Saver createSaver() {
    SlimeSaver saver = SlimeSaver.new()
    saver.setMapper($$mapper)
    return saver
  }

  public Controller createSearcherController(UtilityStage<SearchParameter> stage, Dictionary dictionary) {
    SlimeSearcherController controller = SlimeSearcherController.new(stage)
    controller.prepare((SlimeDictionary)dictionary)
    return controller
  }

  public Controller createIndividualSettingController(UtilityStage<BooleanClass> stage, Dictionary dictionary) {
    SlimeIndividualSettingController controller = SlimeIndividualSettingController.new(stage)
    controller.prepare((SlimeDictionary)dictionary)
    return controller
  }

  public Controller createEditorController(UtilityStage<WordEditResult> stage, Dictionary dictionary, Word word) {
    SlimeEditorController controller = SlimeEditorController.new(stage)
    controller.prepare((SlimeWord)word, (SlimeDictionary)dictionary)
    return controller
  }

  public Controller createCreatorController(UtilityStage<WordEditResult> stage, Dictionary dictionary, Word word) {
    SlimeEditorController controller = SlimeEditorController.new(stage)
    controller.prepare((SlimeWord)word, (SlimeDictionary)dictionary, true)
    return controller
  }

  public Image createIcon() {
    Image icon = Image.new(getClass().getClassLoader().getResourceAsStream(ICON_PATH))
    return icon
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = ObjectMapper.new()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
  }

  public Boolean isCreatable() {
    return true
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
    return SlimeDictionary
  }

}