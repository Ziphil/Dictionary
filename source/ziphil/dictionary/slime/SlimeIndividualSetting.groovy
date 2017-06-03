package ziphil.dictionary.slime

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import ziphil.Launcher
import ziphil.dictionary.IndividualSetting
import ziphil.module.Version
import ziphilib.transform.Ziphilify


@JsonIgnoreProperties(ignoreUnknown=true)
@CompileStatic @Ziphilify
public class SlimeIndividualSetting extends IndividualSetting {

  private static final String SETTING_DIRECTORY = "data/setting/individual/"

  private static ObjectMapper $$mapper = createObjectMapper()

  private String $path = ""
  private List<SlimeSearchParameter> $registeredParameters = ArrayList.new()
  private List<String> $registeredParameterNames = ArrayList.new()
  private Version $version = Version.new(-1, 0, 0)

  private SlimeIndividualSetting() {
  }

  public void save() {
    String compressedPath = createCompressedPath($path)
    FileOutputStream stream = FileOutputStream.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
    $version = Launcher.VERSION
    try {
      $$mapper.writeValue(stream, this)
    } finally {
      stream.close()
    }
  }

  private void ensureCompatibility() {
    if ($version < Version.new(1, 13, 0)) {
      for (SlimeSearchParameter parameter : $registeredParameters) {
        if (parameter != null) {
          if (parameter.getId() > 0) {
            parameter.setHasId(true)
          }
          if (parameter.getName() != null) {
            parameter.setHasName(true)
          }
          if (parameter.getEquivalentName() != null || parameter.getEquivalentTitle() != null) {
            parameter.setHasEquivalent(true)
          }
          if (parameter.getInformationText() != null || parameter.getInformationTitle() != null) {
            parameter.setHasInformation(true)
          }
          if (parameter.getTag() != null) {
            parameter.setHasTag(true)
          }
        }
      }
    }
  }

  public static SlimeIndividualSetting create(SlimeDictionary dictionary) {
    String compressedPath = createCompressedPath(dictionary.getPath())
    File file = File.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
    if (file.exists()) {
      FileInputStream stream = FileInputStream.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
      SlimeIndividualSetting instance
      try {
        instance = $$mapper.readValue(stream, SlimeIndividualSetting)
      } catch (JsonParseException exception) {
        instance = SlimeIndividualSetting.new()
        instance.setPath(dictionary.getPath())
      } finally {
        stream.close()
      }
      instance.ensureCompatibility()
      return instance
    } else {
      SlimeIndividualSetting instance = SlimeIndividualSetting.new()
      instance.setPath(dictionary.getPath())
      return instance
    }
  }

  private static String createCompressedPath(String path) {
    String separator = Launcher.FILE_SEPARATOR.replaceAll("\\\\", "\\\\\\\\")
    String compressedPath = path
    compressedPath = compressedPath.replaceAll(/\.json$/, ".zpdt")
    compressedPath = compressedPath.replaceAll(/\$/, "\\\$d")
    compressedPath = compressedPath.replaceAll(/:/, "\\\$c")
    compressedPath = compressedPath.replaceAll(separator, "\\\$\\\$")
    return compressedPath
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

  public List<SlimeSearchParameter> getRegisteredParameters() {
    return $registeredParameters
  }

  public void setRegisteredParameters(List<SlimeSearchParameter> registeredParameters) {
    $registeredParameters = registeredParameters
  }

  public List<String> getRegisteredParameterNames() {
    return $registeredParameterNames
  }

  public void setRegisteredParameterNames(List<String> registeredParameterNames) {
    $registeredParameterNames = registeredParameterNames
  } 

  public Version getVersion() {
    return $version
  }

  public void setVersion(Version version) {
    $version = version
  }

}