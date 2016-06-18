package ziphil.module

import groovy.transform.CompileStatic
import java.util.regex.Matcher


@Singleton(strict=false)
@CompileStatic @Newify 
public class Setting {

  private static final String SETTINGS_PATH = "data/setting/settings.zpdt"
  private static final String REGISTERED_DICTIONARY_SETTINGS_PATH = "data/setting/dictionaries.zpdt"

  private List<String> $registeredDictionaryPaths = [null] * 10
  private String $defaultDictionaryPath
  private String $contentFontFamily
  private Integer $contentFontSize
  private String $editorFontFamily
  private Integer $editorFontSize
  private Integer $fontRenderingType
  private Boolean $modifiesPunctuation
  private Boolean $savesAutomatically

  private Setting() {
    loadRegisteredDictionarySettings()
    loadSettings()
  }

  private void loadRegisteredDictionarySettings() {
    File file = File.new(REGISTERED_DICTIONARY_SETTINGS_PATH) 
    if (file.exists()) {
      Integer i = 0
      file.eachLine() { String line ->
        Matcher matcher = line =~ /^"(.*)"$/
        if (matcher.matches() && i < 10) {
          String path = matcher.group(1)
          $registeredDictionaryPaths[i] = (path != "") ? path : null
          i += 1
        }
      }
    }
  }

  private void saveRegisteredDictionarySettings() {
    File file = File.new(REGISTERED_DICTIONARY_SETTINGS_PATH)
    StringBuilder output = StringBuilder.new()
    $registeredDictionaryPaths.each() { String path ->
      if (path != null) {
        output.append("\"" + path + "\"\n")
      } else {
        output.append("\"\"\n")
      }
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
          if (type == "default-dictionary-path") {
            $defaultDictionaryPath = data
          } else if (type == "content-font-family") {
            $contentFontFamily = data
          } else if (type == "content-font-size") {
            $contentFontSize = data.toInteger()
          } else if (type == "editor-font-family") {
            $editorFontFamily = data
          } else if (type == "editor-font-size") {
            $editorFontSize = data.toInteger()
          } else if (type == "font-rendering-type") {
            $fontRenderingType = data.toInteger()
          } else if (type == "modifies-punctuation") {
            $modifiesPunctuation = data.toBoolean()
          } else if (type == "saves-automatically") {
            $savesAutomatically = data.toBoolean()
          }
        }
      }
    }
  }

  private void saveSettings() {
    File file = File.new(SETTINGS_PATH)
    StringBuilder output = StringBuilder.new()
    if ($defaultDictionaryPath != null) {
      output.append("default-dictionary-path: \"" + $defaultDictionaryPath + "\"\n")
    }
    if ($contentFontFamily != null) {
      output.append("content-font-family: \"" + $contentFontFamily + "\"\n")
    }
    if ($contentFontSize != null) {
      output.append("content-font-size: \"" + $contentFontSize.toString() + "\"\n")
    }
    if ($editorFontFamily != null) {
      output.append("editor-font-family: \"" + $editorFontFamily + "\"\n")
    }
    if ($editorFontSize != null) {
      output.append("editor-font-size: \"" + $editorFontSize.toString() + "\"\n")
    }
    if ($fontRenderingType != null) {
      output.append("font-rendering-type: \"" + $fontRenderingType.toString() + "\"\n")
    }
    if ($modifiesPunctuation != null) {
      output.append("modifies-punctuation: \"" + $modifiesPunctuation.toString() + "\"\n")
    }
    if ($savesAutomatically != null) {
      output.append("saves-automatically: \"" + $savesAutomatically.toString() + "\"\n")
    }
    file.setText(output.toString(), "UTF-8")
  }

  public void save() {
    saveRegisteredDictionarySettings()
    saveSettings()
  }

  public List<String> getRegisteredDictionaryPaths() {
    return $registeredDictionaryPaths
  }

  public void setRegisteredDictionaryPaths(List<String> registeredDictionaryPaths) {
    $registeredDictionaryPaths = registeredDictionaryPaths
  }

  public String getDefaultDictionaryPath() {
    return $defaultDictionaryPath
  }

  public void setDefaultDictionaryPath(String defaultDictionaryPath) {
    $defaultDictionaryPath = defaultDictionaryPath
  }

  public String getContentFontFamily() {
    return $contentFontFamily
  }

  public void setContentFontFamily(String contentFontFamily) {
    $contentFontFamily = contentFontFamily
  }

  public Integer getContentFontSize() {
    return $contentFontSize
  }

  public void setContentFontSize(Integer contentFontSize) {
    $contentFontSize = contentFontSize
  }

  public String getEditorFontFamily() {
    return $editorFontFamily
  }

  public void setEditorFontFamily(String editorFontFamily) {
    $editorFontFamily = editorFontFamily
  }

  public Integer getEditorFontSize() {
    return $editorFontSize
  }

  public void setEditorFontSize(Integer editorFontSize) {
    $editorFontSize = editorFontSize
  }

  public Integer getFontRenderingType() {
    return $fontRenderingType
  }

  public void setFontRenderingType(Integer fontRenderingType) {
    $fontRenderingType = fontRenderingType
  }

  public Boolean modifiesPunctuation() {
    return $modifiesPunctuation
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

  public Boolean savesAutomatically() {
    return $savesAutomatically
  }

  public void setSavesAutomatically(Boolean savesAutomatically) {
    $savesAutomatically = savesAutomatically
  }

}