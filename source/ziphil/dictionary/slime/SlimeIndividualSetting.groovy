package ziphil.dictionary.slime

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import ziphil.Launcher
import ziphil.dictionary.IndividualSetting
import ziphilib.transform.Ziphilify


@JsonIgnoreProperties(ignoreUnknown=true)
@CompileStatic @Ziphilify
public class SlimeIndividualSetting extends IndividualSetting {

  private static final String SETTING_DIRECTORY = "data/setting/individual/"

  private static ObjectMapper $$mapper = createObjectMapper()

  private String $path = ""
  private List<SlimeSearchParameter> $searchParameters = ArrayList.new(10)

  private SlimeIndividualSetting() {
  }

  public void save() {
    String compressedPath = createCompressedPath($path)
    FileOutputStream stream = FileOutputStream.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
    $$mapper.writeValue(stream, this)
    stream.close()
  }

  public static SlimeIndividualSetting create(SlimeDictionary dictionary) {
    String compressedPath = createCompressedPath(dictionary.getPath())
    File file = File.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
    if (file.exists()) {
      try {
        FileInputStream stream = FileInputStream.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
        SlimeIndividualSetting instance = $$mapper.readValue(stream, SlimeIndividualSetting)
        stream.close()
        return instance
      } catch (JsonParseException exception) {
        return SlimeIndividualSetting.new()
      }
    } else {
      return SlimeIndividualSetting.new()
    }
  }

  private static String createCompressedPath(String path) {
    String separator = File.separator.replaceAll("\\\\", "\\\\\\\\")
    String compressedPath = path.replaceAll(/\$/, "\\\$-").replaceAll(separator, "\\\$\\\$")
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

  public List<SlimeSearchParameter> getSearchParameters() {
    return $searchParameters
  }

  public void setSearchParameters(List<SlimeSearchParameter> searchParameters) {
    $searchParameters = searchParameters
  }

}