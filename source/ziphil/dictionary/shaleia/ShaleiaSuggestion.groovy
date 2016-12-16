package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestion extends SuggestionBase<ShaleiaPossibility> {

  private ShaleiaDictionary $dictionary

  public void update() {
    $isChanged = true
  }

  public void updateContentPane() {
    Setting setting = Setting.getInstance()
    Integer lineSpacing = setting.getLineSpacing()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    ShaleiaSuggestionContentPaneMaker maker = ShaleiaSuggestionContentPaneMaker.new($contentPane, this, $dictionary)
    maker.setLineSpacing(lineSpacing)
    maker.setModifiesPunctuation(modifiesPunctuation)
    maker.make()
    $isChanged = false
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

}