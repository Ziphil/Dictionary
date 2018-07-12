package ziphil.dictionary

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import ziphil.Launcher
import ziphil.module.Version
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class IndividualSetting {

  private static final String SETTING_DIRECTORY = "data/setting/individual/"

  private static ObjectMapper $$mapper = createObjectMapper()

  protected String $path = ""
  protected Version $version = Version.new(-1, 0, 0)
  protected Map<BadgeType, Set<String>> $badgedIdentifiers = EnumMap.new(BadgeType)

  public void save() {
    String savePath = IndividualSetting.createSavePath($path)
    FileOutputStream stream = FileOutputStream.new(savePath)
    $version = Launcher.VERSION
    try {
      $$mapper.writeValue(stream, this)
    } finally {
      stream.close()
    }
  }

  protected static <S extends IndividualSetting> S create(Dictionary dictionary, Class<S> clazz) {
    String savePath = IndividualSetting.createSavePath(dictionary.getPath())
    File file = File.new(savePath)
    if (file.exists()) {
      FileInputStream stream = FileInputStream.new(savePath)
      IndividualSetting instance
      try {
        instance = $$mapper.readValue(stream, clazz)
      } catch (Exception exception) {
        instance = clazz.getConstructor().newInstance()
        instance.setPath(dictionary.getPath())
      } finally {
        stream.close()
      }
      return instance
    } else {
      IndividualSetting instance = clazz.getConstructor().newInstance()
      instance.setPath(dictionary.getPath())
      return instance
    }
  }

  private static String createSavePath(String path) {
    String separator = Launcher.FILE_SEPARATOR.replaceAll("\\\\", "\\\\\\\\")
    String compressedPath = path
    compressedPath = compressedPath.replaceAll(/\.\w+$/, ".zpdt")
    compressedPath = compressedPath.replaceAll(/\$/, "\\\$d")
    compressedPath = compressedPath.replaceAll(/:/, "\\\$c")
    compressedPath = compressedPath.replaceAll(separator, "\\\$\\\$")
    return Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = ObjectMapper.new()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
  }

  public String getPath() {
    return $path
  }

  public void setPath(String path) {
    $path = path
  }

  public Version getVersion() {
    return $version
  }

  public void setVersion(Version version) {
    $version = version
  }

  public Map<BadgeType, Set<String>> getBadgedIdentifiers() {
    return $badgedIdentifiers
  }

  public void setBadgedIdentifiers(Map<BadgeType, Set<String>> badgedIdentifiers) {
    $badgedIdentifiers = badgedIdentifiers
  }

  public abstract List<SearchParameter> getRegisteredParameters()

  public abstract List<String> getRegisteredParameterNames()

}