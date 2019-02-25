package ziphil.plugin.builtin

import groovy.transform.CompileStatic
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.stage.StageStyle
import ziphil.controller.Controller
import ziphil.custom.Dialog
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.slime.SlimeDictionary
import ziphil.dictionary.slime.SlimeRelation
import ziphil.dictionary.slime.SlimeWord
import ziphil.plugin.SimplePlugin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeRenumberNumbersPlugin implements SimplePlugin {

  private static final String NAME = "IDの振り直し"

  public void call(Dictionary dictionary) {
    if (dictionary instanceof SlimeDictionary) {
      Dialog dialog = Dialog.new(StageStyle.UTILITY) 
      dialog.setTitle(Controller.DIALOG_RESOURCES.getString("title.confirmRenumberNumbers"))
      dialog.setContentText(Controller.DIALOG_RESOURCES.getString("contentText.confirmRenumberNumbers"))
      dialog.showAndWait()
      if (dialog.isCommitted()) {
        renumberNumbers(dictionary)
      }
    }
  }

  private void renumberNumbers(SlimeDictionary dictionary) {
    Map<IntegerClass, IntegerClass> numbers = HashMap.new()
    for (SlimeWord word : dictionary.getRawWords()) {
      Int number = word.getNumber()
      numbers[number] = 0
    }
    Int[] previousNumbers = numbers.keySet().toArray(Int[].new(0))
    Arrays.sort(previousNumbers)
    for (Int i = 0 ; i < numbers.size() ; i ++) {
      numbers[previousNumbers[i]] = i + 1
    }
    for (SlimeWord word : dictionary.getRawWords()) {
      word.setNumber(numbers[word.getNumber()])
    }
    Collections.sort(dictionary.getRawWords()) { SlimeWord firstWord, SlimeWord secondWord ->
      return firstWord.getNumber() <=> secondWord.getNumber()
    }
    dictionary.updateFirst()
  }

  public Boolean isSupported(Dictionary dictionary) {
    if (dictionary instanceof SlimeDictionary) {
      return true
    } else {
      return false
    }
  }

  public String getName() {
    return NAME
  }

  public KeyCode getKeyCode() {
    return null
  }

  public Image getIcon() {
    return null
  }

}