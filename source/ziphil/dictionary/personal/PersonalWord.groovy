package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactoryBase
import ziphil.dictionary.WordBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWord extends WordBase {

  private PersonalDictionary $dictionary
  private String $pronunciation = ""
  private String $translation = ""
  private String $usage = ""
  private Integer $level = 0
  private Integer $memory = 0
  private Integer $modification = 0

  public void update() {
    updateContent()
    changeContentPaneFactory()
  }

  private void updateContent() {
    $content = name + "\n" + translation + "\n" + usage
  }

  protected ContentPaneFactoryBase createContentPaneFactory() {
    return PersonalWordContentPaneFactory.new(this, $dictionary)
  }

  protected ContentPaneFactoryBase createPlainContentPaneFactory() {
    return PersonalWordPlainContentPaneFactory.new(this, $dictionary)
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

  public void setPronunciation(String pronunciation) {
    $pronunciation = pronunciation
  }

  public String getTranslation() {
    return $translation
  }

  public void setTranslation(String translation) {
    $translation = translation
  }

  public String getUsage() {
    return $usage
  }

  public void setUsage(String usage) {
    $usage = usage
  }

  public Integer getLevel() {
    return $level
  }

  public void setLevel(Integer level) {
    $level = level
  }

  public Integer getMemory() {
    return $memory
  }

  public void setMemory(Integer memory) {
    $memory = memory
  }

  public Integer getModification() {
    return $modification
  }

  public void setModification(Integer modification) {
    $modification = modification
  }

}