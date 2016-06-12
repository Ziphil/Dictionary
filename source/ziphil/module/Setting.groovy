package ziphil.module

import groovy.transform.CompileStatic
import java.util.regex.Matcher


@Singleton(strict=false)
@CompileStatic @Newify 
public class Setting {

  private static final String SETTINGS_PATH = "data/settings.zpdt"
  private static final String DICTIONARY_SETTINGS_PATH = "data/dictionaries.zpdt"

  private List<DictionarySetting> $dictionarySettings = ArrayList.new()
  private String $defaultDictionaryName
  private String $contentFontName
  private Integer $contentFontSize
  private String $editorFontName
  private Integer $editorFontSize

  private Setting() {
    loadDictionarySettings()
    loadSettings()
  }

  private void loadDictionarySettings() {
    File file = File.new(DICTIONARY_SETTINGS_PATH)
    if (file.exists()) {
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^"(.*)",\s*"(.*)",\s*"(.*)"$/
        if (matcher.matches()) {
          DictionarySetting data = DictionarySetting.new(matcher.group(1), matcher.group(2), matcher.group(3))
          $dictionarySettings.add(data)
        }
      }
    }
  }

  private void saveDictionarySettings() {
    File file = File.new(DICTIONARY_SETTINGS_PATH)
    StringBuilder output = StringBuilder.new()
    $dictionarySettings.each() { DictionarySetting dictionarySetting ->
      output.append("\"" + dictionarySetting.getName() + "\", ")
      output.append("\"" + dictionarySetting.getTypeName() + "\", ")
      output.append("\"" + dictionarySetting.getPath() + "\"\n")
    }
    file.setText(output.toString(), "UTF-8")
  }

  private void loadSettings() {
    File file = File.new(SETTINGS_PATH)
    if (file.exists()) {
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^([a-z_-]+):\s*"(.+)"$/
        if (matcher.matches()) {
          String type = matcher.group(1)
          String data = matcher.group(2)
          if (type == "default-dictionary-name") {
            $defaultDictionaryName = data
          } else if (type == "content-font-name") {
            $contentFontName = data
          } else if (type == "content-font-size") {
            $contentFontSize = data.toInteger()
          } else if (type == "editor-font-name") {
            $editorFontName = data
          } else if (type == "editor-font-size") {
            $editorFontSize = data.toInteger()
          }
        }
      }
    }
  }

  private void saveSettings() {
    File file = File.new(SETTINGS_PATH)
    StringBuilder output = StringBuilder.new()
    if ($defaultDictionaryName != null) {
      output.append("default-dictionary-name: " + $defaultDictionaryName + "\n")
    }
    if ($contentFontName != null) {
      output.append("content-font-name: " + $contentFontName + "\n")
    }
    if ($contentFontSize != null) {
      output.append("content-font-size: " + $contentFontSize.toString() + "\n")
    }
    if ($editorFontName != null) {
      output.append("editor-font-name: " + $editorFontName + "\n")
    }
    if ($editorFontSize != null) {
      output.append("editor-font-size: " + $editorFontSize.toString() + "\n")
    }
    file.setText(output.toString(), "UTF-8")
  }

  public void save() {
    saveDictionarySettings()
    saveSettings()
  }

  public List<DictionarySetting> getDictionarySettings() {
    return $dictionarySettings
  }

  public void setDictionarySettings(List<DictionarySetting> dictionarySettings) {
    $dictionarySettings = dictionarySettings
  }

  public String getDefaultDictionaryName() {
    return $defaultDictionaryName
  }

  public void setDefaultDictionaryName(String defaultDictionaryName) {
    $defaultDictionaryName = defaultDictionaryName
  }

  public String getContentFontName() {
    return $contentFontName
  }

  public void setContentFontName(String contentFontName) {
    $contentFontName = contentFontName
  }

  public Integer getContentFontSize() {
    return $contentFontSize
  }

  public void setContentFontSize(Integer contentFontSize) {
    $contentFontSize = contentFontSize
  }

  public String getEditorFontName() {
    return $editorFontName
  }

  public void setEditorFontName(String editorFontName) {
    $editorFontName = editorFontName
  }

  public Integer getEditorFontSize() {
    return $editorFontSize
  }

  public void setEditorFontSize(Integer editorFontSize) {
    $editorFontSize = editorFontSize
  }

}