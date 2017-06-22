package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import ziphil.Launcher
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EquivalentCollection {

  private static final String COLLECTION_DIRECTORY = "data/collection/"

  private static ObjectMapper $$mapper = createObjectMapper()

  private String $name = ""
  private List<PseudoWord> $pseudoWords = ArrayList.new()

  public static EquivalentCollection load(String path) {
    File file = File.new(Launcher.BASE_PATH + COLLECTION_DIRECTORY + path)
    if (file.exists()) {
      FileInputStream stream = FileInputStream.new(file)
      EquivalentCollection collection
      try {
        collection = $$mapper.readValue(stream, EquivalentCollection)
      } catch (JsonParseException exception) {
        collection = EquivalentCollection.new()
      } finally {
        stream.close()
      }
      return collection
    } else {
      EquivalentCollection collection = EquivalentCollection.new()
      return collection
    }
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = ObjectMapper.new()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  @JsonProperty("data")
  public List<PseudoWord> getPseudoWords() {
    return $pseudoWords
  }

  @JsonProperty("data")
  public void setPseudoWords(List<PseudoWord> pseudoWords) {
    $pseudoWords = pseudoWords
  }

}