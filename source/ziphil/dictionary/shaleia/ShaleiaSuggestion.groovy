package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.Suggestion
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestion extends Suggestion<ShaleiaPossibility> {

  private ShaleiaDictionary $dictionary

  public ShaleiaSuggestion() {
    update()
  }

  public void update() {
    $isChanged = true
  }

  public void createContentPane() {
    Setting setting = Setting.getInstance()
    Integer lineSpacing = setting.getLineSpacing()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    ShaleiaSuggestionContentPaneCreator creator = ShaleiaSuggestionContentPaneCreator.new($contentPane, this, $dictionary)
    creator.setLineSpacing(lineSpacing)
    creator.setModifiesPunctuation(modifiesPunctuation)
    creator.create()
    $isChanged = false
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

}