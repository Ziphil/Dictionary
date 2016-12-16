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

  public void updateContentPane() {
    if ($isChanged) {
      Setting setting = Setting.getInstance()
      Integer lineSpacing = setting.getLineSpacing()
      Boolean modifiesPunctuation = setting.getModifiesPunctuation()
      SlimeSuggestionContentPaneMaker maker = SlimeSuggestionContentPaneMaker.new($contentPane, this, $dictionary)
      maker.setLineSpacing(lineSpacing)
      maker.setModifiesPunctuation(modifiesPunctuation)
      maker.make()
      $isChanged = false
    }
  }

  public SlimeDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(SlimeDictionary dictionary) {
    $dictionary = dictionary
  }

}