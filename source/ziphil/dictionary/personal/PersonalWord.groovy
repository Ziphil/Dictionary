package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.Word
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWord extends Word {

  private PersonalDictionary $dictionary
  private String $pronunciation = ""
  private String $translation = ""
  private String $usage = ""
  private Integer $level = 0
  private Integer $memory = 0
  private Integer $modification = 0

  public PersonalWord(String name, String pronunciation, String translation, String usage, Integer level, Integer memory, Integer modification) {
    update(name, pronunciation, translation, usage, level, memory, modification)
  }

  public void update(String name, String pronunciation, String translation, String usage, Integer level, Integer memory, Integer modification) {
    $name = name
    $pronunciation = pronunciation
    $translation = translation
    $usage = usage
    $level = level
    $memory = memory
    $modification = modification
    $content = name + "\n" + translation + "\n" + usage
    $isChanged = true
  }

  public void createContentPane() {
    Setting setting = Setting.getInstance()
    Boolean modifiesPunctuation = setting.getModifiesPunctuation()
    PersonalWordContentPaneCreator creator = PersonalWordContentPaneCreator.new($contentPane, this, $dictionary)
    creator.setModifiesPunctuation(modifiesPunctuation)
    creator.create()
    $isChanged = false
  }

  public PersonalDictionary getDictionary() {
    return $dictionary
  }

  public void setDictionary(PersonalDictionary dictionary) {
    $dictionary = dictionary
  }

  public String getPronunciation() {
    return $pronunciation
  }

  public String getTranslation() {
    return $translation
  }

  public String getUsage() {
    return $usage
  }

  public Integer getLevel() {
    return $level
  }

  public Integer getMemory() {
    return $memory
  }

  public Integer getModification() {
    return $modification
  }

}