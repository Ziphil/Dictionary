package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.ContentPaneFactory
import ziphil.dictionary.WordBase
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalWord extends WordBase {

  private PersonalDictionary $dictionary
  private String $pronunciation = ""
  private String $translation = ""
  private String $usage = ""
  private Int $level = 0
  private Int $memory = 0
  private Int $modification = 0

  public void update() {
    updateContent()
    changeContentPaneFactory()
  }

  private void updateContent() {
    $content = name + "\n" + translation + "\n" + usage
  }

  protected ContentPaneFactory createContentPaneFactory() {
    return PersonalWordContentPaneFactory.new(this, $dictionary)
  }

  protected ContentPaneFactory createPlainContentPaneFactory() {
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

  public Int getLevel() {
    return $level
  }

  public void setLevel(Int level) {
    $level = level
  }

  public Int getMemory() {
    return $memory
  }

  public void setMemory(Int memory) {
    $memory = memory
  }

  public Int getModification() {
    return $modification
  }

  public void setModification(Int modification) {
    $modification = modification
  }

}