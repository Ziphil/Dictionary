package ziphil.dictionary

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import ziphil.Launcher
import ziphil.module.Version
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public abstract class IndividualSetting {

  protected static final String SETTING_DIRECTORY = "data/setting/individual/"

  protected static ObjectMapper $$mapper = createObjectMapper()

  protected String $path = ""
  protected Version $version = Version.new(-1, 0, 0)

  public void save() {
    String compressedPath = IndividualSetting.createCompressedPath($path)
    FileOutputStream stream = FileOutputStream.new(Launcher.BASE_PATH + SETTING_DIRECTORY + compressedPath)
    $version = Launcher.VERSION
    try {
      $$mapper.writeValue(stream, this)
    } finally {
      stream.close()
    }
  }

  protected static String createCompressedPath(String path) {
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

  public Version getVersion() {
    return $version
  }

  public void setVersion(Version version) {
    $version = version
  }

  public abstract List<SearchParameter> getRegisteredParameters()

  public abstract List<String> getRegisteredParameterNames()

}