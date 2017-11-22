package ziphil.plugin

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
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeRenumberIdsPlugin implements Plugin {

  private static final String NAME = "IDの振り直し"

  public void call(Dictionary dictionary) {
    if (dictionary instanceof SlimeDictionary) {
      Dialog dialog = Dialog.new(StageStyle.UTILITY) 
      dialog.setTitle(Controller.DIALOG_RESOURCES.getString("title.confirmRenumberIds"))
      dialog.setContentText(Controller.DIALOG_RESOURCES.getString("contentText.confirmRenumberIds"))
      dialog.showAndWait()
      if (dialog.isCommitted()) {
        renumberIds(dictionary)
      }
    }
  }

  private void renumberIds(SlimeDictionary dictionary) {
    Map<IntegerClass, IntegerClass> ids = HashMap.new()
    for (SlimeWord word : dictionary.getRawWords()) {
      Int id = word.getId()
      ids[id] = 0
    }
    Int[] previousIds = ids.keySet().toArray(Int[].new(0))
    Arrays.sort(previousIds)
    for (Int i = 0 ; i < ids.size() ; i ++) {
      ids[previousIds[i]] = i + 1
    }
    for (SlimeWord word : dictionary.getRawWords()) {
      word.setId(ids[word.getId()])
      for (SlimeRelation relation : word.getRelations()) {
        relation.setId(ids[relation.getId()])
      }
    }
    Collections.sort(dictionary.getRawWords()) { SlimeWord firstWord, SlimeWord secondWord ->
      return firstWord.getId() <=> secondWord.getId()
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