package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimeSuggestion extends SuggestionBase<SlimePossibility> {

  private SlimeDictionary $dictionary

  public void update() {
    $isChanged = true
  }

  public void createContentPane() {
    Setting setting = Setting.getInstance()
    Integer lineSpacing = setting.getLineSpacing()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    SlimeSuggestionContentPaneCreator creator = SlimeSuggestionContentPaneCreator.new($contentPane, this, $dictionary)
    creator.setLineSpacing(lineSpacing)
    creator.setModifiesPunctuation(modifiesPunctuation)
    creator.create()
    $isChanged = false
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}