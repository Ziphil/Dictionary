package ziphil.module

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphil.Launcher
import ziphilib.transform.Ziphilify


@JsonIgnoreProperties(ignoreUnknown=true)
@CompileStatic @Ziphilify
public class Setting {

  private static final String CORRECT_PASSWORD = "fkdocwpvmdcaskex"
  private static final String SETTING_PATH = "data/setting/setting.zpdt"
  private static final String CUSTOM_STYLESHEET_PATH = "data/setting/custom.css"
  public static final String CUSTOM_STYLESHEET_URL = createCustomStylesheetURL()

  private static ObjectMapper $$mapper = createObjectMapper()
  private static Setting $$instance = createInstance()

  private List<String> $registeredDictionaryPaths = ArrayList.new()
  private List<String> $registeredDictionaryNames = ArrayList.new()
  private String $defaultDictionaryPath
  private String $contentFontFamily
  private Integer $contentFontSize
  private String $editorFontFamily
  private Integer $editorFontSize
  private String $systemFontFamily
  private Integer $lineSpacing = 0
  private Integer $separativeInterval = 700
  private FontRenderingType $fontRenderingType = FontRenderingType.DEFAULT_LCD
  private Boolean $modifiesPunctuation = false
  private Boolean $savesAutomatically = false
  private Boolean $ignoresAccent = false
  private Boolean $ignoresCase = false
  private Boolean $searchesPrefix = true
  private Boolean $ignoresDuplicateSlimeId = true
  private Boolean $showsSlimeId = false
  private String $password = ""
  private Version $version = Version.new(-1, 0, 0)

  public void save() {
    saveSetting()
    saveCustomStylesheet()
  }

  private void saveSetting() {
    FileOutputStream stream = FileOutputStream.new(Launcher.BASE_PATH + SETTING_PATH)
    $version = Launcher.VERSION
    try {
      $$mapper.writeValue(stream, this)
    } finally {
      stream.close()
    }
  }

  private void saveCustomStylesheet() {
    File file = File.new(Launcher.BASE_PATH + CUSTOM_STYLESHEET_PATH)
    StringBuilder stylesheet = StringBuilder.new()
    if ($contentFontFamily != null || $contentFontSize != null) {
      stylesheet.append("#dictionary-list .content-pane {\n")
      if ($contentFontFamily != null) {
        stylesheet.append("  -fx-font-family: \"")
        stylesheet.append(Strings.escapeUnicode($contentFontFamily))
        stylesheet.append("\";\n")
      }
      if ($contentFontSize != null) {
        stylesheet.append("  -fx-font-size:")
        stylesheet.append($contentFontSize)
        stylesheet.append(";\n")
      }
      stylesheet.append("}\n\n")
    }
    if ($editorFontFamily != null || $editorFontSize != null) {
      stylesheet.append(".editor {\n")
      if ($editorFontFamily != null) {
        stylesheet.append("  -fx-font-family: \"")
        stylesheet.append(Strings.escapeUnicode($editorFontFamily))
        stylesheet.append("\";\n")
      }
      if ($editorFontSize != null) {
        stylesheet.append("  -fx-font-size: ")
        stylesheet.append($editorFontSize)
        stylesheet.append(";\n")
      }
      stylesheet.append("}\n\n")
    }
    if ($systemFontFamily != null) {
      stylesheet.append(".root {\n")
      stylesheet.append("  -fx-font-family: \"")
      stylesheet.append(Strings.escapeUnicode($systemFontFamily))
      stylesheet.append("\";\n")
      stylesheet.append("}\n\n")
    }
    file.setText(stylesheet.toString(), "UTF-8")
  }

  private static Setting createInstance() {
    File file = File.new(Launcher.BASE_PATH + SETTING_PATH)
    if (file.exists()) {
      FileInputStream stream = FileInputStream.new(Launcher.BASE_PATH + SETTING_PATH)
      Setting instance
      try {
        instance = $$mapper.readValue(stream, Setting)
      } catch (JsonParseException exception) {
        instance = Setting.new()
      } finally {
        stream.close()
      }
      return instance
    } else {
      Setting instance = Setting.new()
      return instance
    }
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = ObjectMapper.new()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
  }

  public static String createCustomStylesheetURL() {
    URL url = File.new(Launcher.BASE_PATH + CUSTOM_STYLESHEET_PATH).toURI().toURL()
    return url.toString()
  }

  @JsonIgnore
  public Boolean isDebugging() {
    return $password == CORRECT_PASSWORD
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

  public List<String> getRegisteredDictionaryNames() {
    return $registeredDictionaryNames
  }

  public void setRegisteredDictionaryNames(List<String> registeredDictionaryNames) {
    $registeredDictionaryNames = registeredDictionaryNames
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

  public String getSystemFontFamily() {
    return $systemFontFamily
  }

  public void setSystemFontFamily(String systemFontFamily) {
    $systemFontFamily = systemFontFamily
  }

  public Integer getLineSpacing() {
    return $lineSpacing
  }

  public void setLineSpacing(Integer lineSpacing) {
    $lineSpacing = lineSpacing
  }

  public Integer getSeparativeInterval() {
    return $separativeInterval
  }

  public void setSeparativeInterval(Integer separativeInterval) {
    $separativeInterval = separativeInterval
  }

  public FontRenderingType getFontRenderingType() {
    return $fontRenderingType
  }

  public void setFontRenderingType(FontRenderingType fontRenderingType) {
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

  public Boolean getSearchesPrefix() {
    return $searchesPrefix
  }

  public void setSearchesPrefix(Boolean searchesPrefix) {
    $searchesPrefix = searchesPrefix
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

  public String getPassword() {
    return $password
  }

  public void setPassword(String password) {
    $password = password
  }

  public Version getVersion() {
    return $version
  }

  public void setVersion(Version version) {
    $version = version
  }

}