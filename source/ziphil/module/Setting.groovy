package ziphil.module

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import net.arnx.jsonic.JSON
import net.arnx.jsonic.JSONException
import ziphil.Launcher


@CompileStatic @Newify 
public class Setting {

  private static final String SETTING_PATH = "data/setting/setting.zpdt"
  private static final String CUSTOM_STYLESHEET_PATH = "data/setting/custom.css"
  public static final String CUSTOM_STYLESHEET_URL = createCustomStylesheetURL()

  private static Setting $$instance = createInstance()

  private List<String> $registeredDictionaryPaths = ArrayList.new()
  private String $defaultDictionaryPath
  private String $contentFontFamily
  private Integer $contentFontSize
  private String $editorFontFamily
  private Integer $editorFontSize
  private Integer $fontRenderingType
  private Boolean $modifiesPunctuation = false
  private Boolean $savesAutomatically = false
  private Boolean $ignoresAccent = false
  private Boolean $ignoresCase = false
  private Boolean $prefixSearch = true
  private Boolean $ignoresDuplicateSlimeId = true
  private Boolean $showsSlimeId = false

  public void save() {
    saveSetting()
    saveCustomStylesheet()
  }

  private void saveSetting() {
    FileOutputStream stream = FileOutputStream.new(Launcher.BASE_PATH + SETTING_PATH)
    JSON json = JSON.new()
    json.setPrettyPrint(true)
    json.setIndentText("  ")
    json.format(this, stream)
    stream.close()
  }

  private void saveCustomStylesheet() {
    File file = File.new(Launcher.BASE_PATH + CUSTOM_STYLESHEET_PATH)
    StringBuilder stylesheet = StringBuilder.new()
    if ($contentFontFamily != null && $contentFontSize != null) {
      stylesheet.append("#dictionary-list .content-pane {\n")
      stylesheet.append("  -fx-font-family: \"${Strings.escapeUnicode($contentFontFamily)}\";\n")
      stylesheet.append("  -fx-font-size: ${$contentFontSize};\n")
      stylesheet.append("}\n\n")
    }
    if ($editorFontFamily != null && $editorFontSize != null) {
      stylesheet.append(".editor {\n")
      stylesheet.append("  -fx-font-family: \"${Strings.escapeUnicode($editorFontFamily)}\";\n")
      stylesheet.append("  -fx-font-size: ${$editorFontSize};\n")
      stylesheet.append("}\n\n")
    }    
    file.setText(stylesheet.toString(), "UTF-8")
  }

  public static Setting createInstance() {
    File file = File.new(Launcher.BASE_PATH + SETTING_PATH)
    if (file.exists()) {
      try {
        FileInputStream stream = FileInputStream.new(Launcher.BASE_PATH + SETTING_PATH)
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

  public static String createCustomStylesheetURL() {
    URL url = File.new(Launcher.BASE_PATH + CUSTOM_STYLESHEET_PATH).toURI().toURL()
    return url.toString()
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

  public Boolean getPrefixSearch() {
    return $prefixSearch
  }

  public void setPrefixSearch(Boolean prefixSearch) {
    $prefixSearch = prefixSearch
  }

  public Boolean getIgnoresDuplicateSlimeId() {
    return $ignoresDuplicateSlimeId
  }

  public void setIgnoresDuplicateSlimeId(Boolean ignoresDuplicateSlimeId) {
    $ignoresDuplicateSlimeId = ignoresDuplicateSlimeId
  }

  public Boolean getShowsSlimeId() {
    return $showsSlimeId
  }

  public void setShowsSlimeId(Boolean showsSlimeId) {
    $showsSlimeId = showsSlimeId
  }

}