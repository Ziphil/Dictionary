package ziphil.module

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import net.arnx.jsonic.JSON
import net.arnx.jsonic.JSONException


@CompileStatic @Newify 
public class Setting {

  private static final String SETTINGS_PATH = "data/setting/settings.zpdt"

  private static Setting $$instance = createInstance()

  private List<String> $registeredDictionaryPaths = ArrayList.new()
  private String $defaultDictionaryPath
  private String $contentFontFamily
  private Integer $contentFontSize
  private String $editorFontFamily
  private Integer $editorFontSize
  private Integer $fontRenderingType
  private Boolean $modifiesPunctuation
  private Boolean $savesAutomatically
  private Boolean $ignoresAccent
  private Boolean $ignoresCase

  public void save() {
    FileOutputStream stream = FileOutputStream.new(SETTINGS_PATH)
    JSON json = JSON.new()
    json.setPrettyPrint(true)
    json.setIndentText("  ")
    json.format(this, stream)
    stream.close()
  }

  public static Setting createInstance() {
    File file = File.new(SETTINGS_PATH)
    if (file.exists()) {
      try {
        FileInputStream stream = FileInputStream.new(SETTINGS_PATH)
        JSON json = JSON.new()
        Setting instance = json.parse(stream, Setting)
        stream.close()
        return instance
      } catch (JSONException exception) {
        return Setting.new()
      }
    } else {
      return Setting.new()
    }
  }

  public static Setting getInstance() {
    return $$instance
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

  public Boolean getModifiesPunctuation() {
    return $modifiesPunctuation
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

  public Boolean getSavesAutomatically() {
    return $savesAutomatically
  }

  public void setSavesAutomatically(Boolean savesAutomatically) {
    $savesAutomatically = savesAutomatically
  }

  public Boolean getIgnoresAccent() {
    return $ignoresAccent
  }

  public void setIgnoresAccent(Boolean ignoresAccent) {
    $ignoresAccent = ignoresAccent
  }

  public Boolean getIgnoresCase() {
    return $ignoresCase
  }

  public void setIgnoresCase(Boolean ignoresCase) {
    $ignoresCase = ignoresCase
  }

}