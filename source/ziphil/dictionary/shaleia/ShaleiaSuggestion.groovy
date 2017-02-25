package ziphil.dictionary.shaleia

import groovy.transform.CompileStatic
import ziphil.dictionary.SuggestionBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaSuggestion extends SuggestionBase<ShaleiaPossibility> {

  private ShaleiaDictionary $dictionary

  public void update() {
    changeContentPaneFactory()
  }

  protected void makeContentPaneFactory() {
    Setting setting = Setting.getInstance()
    Integer lineSpacing = setting.getLineSpacing()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    $contentPaneFactory = ShaleiaSuggestionContentPaneFactory.new(this, $dictionary)
    $contentPaneFactory.setLineSpacing(lineSpacing)
    $contentPaneFactory.setModifiesPunctuation(modifiesPunctuation)
  }

  public ShaleiaDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(ShaleiaDictionary dictionary) {
    $dictionary = dictionary
  }

}