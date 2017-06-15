package ziphil.module

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import java.util.regex.Matcher
import javafx.scene.text.Font
import ziphil.Launcher
import ziphilib.transform.Ziphilify


@JsonIgnoreProperties(ignoreUnknown=true)
@CompileStatic @Ziphilify
public class Setting {

  private static final String CORRECT_PASSWORD = "fkdocwpvmdcaskex"
  private static final String SETTING_PATH = "data/setting/setting.zpdt"
  private static final String CUSTOM_STYLESHEET_PATH = "data/setting/custom.css"
  private static final String CUSTOM_WEB_VIEW_STYLESHEET_PATH = "data/setting/web_custom.css"
  public static final String CUSTOM_STYLESHEET_URL = createCustomStylesheetURL()
  public static final String CUSTOM_WEB_VIEW_STYLESHEET_URL = createCustomWebViewStylesheetURL()

  private static ObjectMapper $$mapper = createObjectMapper()
  private static Setting $$instance = createInstance()

  private List<String> $registeredDictionaryPaths = ArrayList.new()
  private List<String> $registeredDictionaryNames = ArrayList.new()
  private String $defaultDictionaryPath
  private String $contentFontFamily
  private Int $contentFontSize
  private String $editorFontFamily
  private Int $editorFontSize
  private String $systemFontFamily
  private Int $lineSpacing = 0
  private Int $separativeInterval = 700
  private String $scriptName = "groovy"
  private FontRenderingType $fontRenderingType = FontRenderingType.DEFAULT_LCD
  private ClickType $linkClickType = ClickType.PRIMARY
  private Boolean $modifiesPunctuation = false
  private Boolean $keepsMainOnTop = false
  private Boolean $keepsEditorOnTop = true
  private Boolean $savesAutomatically = false
  private Boolean $ignoresAccent = false
  private Boolean $ignoresCase = false
  private Boolean $searchesPrefix = true
  private Boolean $ignoresDuplicateSlimeId = true
  private Boolean $showsSlimeId = false
  private Boolean $asksMutualRelation = true
  private Boolean $persistsPanes = false
  private String $password = ""
  private Version $version = Version.new(-1, 0, 0)

  public void save() {
    saveSetting()
    saveCustomStylesheet()
    saveCustomWebViewStylesheet()
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
    BufferedWriter writer = file.newWriter("UTF-8")
    try {
      if ($contentFontFamily != null || $contentFontSize > 0) {
        writer.write(".dictionary-list .content-pane {\n")
        if ($contentFontFamily != null) {
          writer.write("  -fx-font-family: \"")
          writer.write(Strings.escapeUnicode($contentFontFamily))
          writer.write("\";\n")
        }
        if ($contentFontSize > 0) {
          writer.write("  -fx-font-size: ")
          writer.write($contentFontSize.toString())
          writer.write(";\n")
        }
        writer.write("}\n\n")
      }
      if ($editorFontFamily != null || $editorFontSize > 0) {
        writer.write(".editor {\n")
        if ($editorFontFamily != null) {
          writer.write("  -fx-font-family: \"")
          writer.write(Strings.escapeUnicode($editorFontFamily))
          writer.write("\";\n")
        }
        if ($editorFontSize > 0) {
          writer.write("  -fx-font-size: ")
          writer.write($editorFontSize.toString())
          writer.write(";\n")
        }
        writer.write("}\n\n")
        writer.write(".editor * {\n")
        writer.write("  -fx-font-family: \"")
        if ($systemFontFamily != null) {
          writer.write(Strings.escapeUnicode($systemFontFamily))
        } else {
          String defaultSystemFontFamily = Font.getDefault().getFamily()
          writer.write(Strings.escapeUnicode(defaultSystemFontFamily))
        }
        writer.write("\";\n")
        writer.write("}\n\n")
      }
      if ($systemFontFamily != null) {
        writer.write(".root {\n")
        writer.write("  -fx-font-family: \"")
        writer.write(Strings.escapeUnicode($systemFontFamily))
        writer.write("\";\n")
        writer.write("}\n\n")
      }
    } finally {
      writer.close()
    }
  }

  private void saveCustomWebViewStylesheet() {
    File file = File.new(Launcher.BASE_PATH + CUSTOM_WEB_VIEW_STYLESHEET_PATH)
    BufferedWriter writer = file.newWriter("UTF-8")
    try {
      writer.write("body {\n")
      writer.write("  font-family: \"")
      if ($systemFontFamily != null) {
        writer.write(Strings.escapeUnicode($systemFontFamily))
      } else {
        String defaultSystemFontFamily = Font.getDefault().getFamily()
        writer.write(Strings.escapeUnicode(defaultSystemFontFamily))
      }
      writer.write("\";\n")
      writer.write("}\n\n")
    } finally {
      writer.close()
    }
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

  public static String createCustomWebViewStylesheetURL() {
    URL url = File.new(Launcher.BASE_PATH + CUSTOM_WEB_VIEW_STYLESHEET_PATH).toURI().toURL()
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

  public Int getContentFontSize() {
    return $contentFontSize
  }

  public void setContentFontSize(Int contentFontSize) {
    $contentFontSize = contentFontSize
  }

  public String getEditorFontFamily() {
    return $editorFontFamily
  }

  public void setEditorFontFamily(String editorFontFamily) {
    $editorFontFamily = editorFontFamily
  }

  public Int getEditorFontSize() {
    return $editorFontSize
  }

  public void setEditorFontSize(Int editorFontSize) {
    $editorFontSize = editorFontSize
  }

  public String getSystemFontFamily() {
    return $systemFontFamily
  }

  public void setSystemFontFamily(String systemFontFamily) {
    $systemFontFamily = systemFontFamily
  }

  public Int getLineSpacing() {
    return $lineSpacing
  }

  public void setLineSpacing(Int lineSpacing) {
    $lineSpacing = lineSpacing
  }

  public Int getSeparativeInterval() {
    return $separativeInterval
  }

  public void setSeparativeInterval(Int separativeInterval) {
    $separativeInterval = separativeInterval
  }

  public String getScriptName() {
    return $scriptName
  }

  public void setScriptName(String scriptName) {
    $scriptName = scriptName
  }

  public FontRenderingType getFontRenderingType() {
    return $fontRenderingType
  }

  public void setFontRenderingType(FontRenderingType fontRenderingType) {
    $fontRenderingType = fontRenderingType
  }

  public ClickType getLinkClickType() {
    return $linkClickType
  }

  public void setLinkClickType(ClickType linkClickType) {
    $linkClickType = linkClickType
  }

  public Boolean getModifiesPunctuation() {
    return $modifiesPunctuation
  }

  public void setModifiesPunctuation(Boolean modifiesPunctuation) {
    $modifiesPunctuation = modifiesPunctuation
  }

  public Boolean getKeepsMainOnTop() {
    return $keepsMainOnTop
  }

  public void setKeepsMainOnTop(Boolean keepsMainOnTop) {
    $keepsMainOnTop = keepsMainOnTop
  }

  public Boolean getKeepsEditorOnTop() {
    return $keepsEditorOnTop
  }

  public void setKeepsEditorOnTop(Boolean keepsEditorOnTop) {
    $keepsEditorOnTop = keepsEditorOnTop
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

  public Boolean getAsksMutualRelation() {
    return $asksMutualRelation
  }

  public void setAsksMutualRelation(Boolean asksMutualRelation) {
    $asksMutualRelation = asksMutualRelation
  }

  public Boolean getPersistsPanes() {
    return $persistsPanes
  }

  public void setPersistsPanes(Boolean persistsPanes) {
    $persistsPanes = persistsPanes
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