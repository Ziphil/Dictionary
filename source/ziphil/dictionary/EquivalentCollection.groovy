package ziphil.dictionary

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EquivalentCollection {

  private static ObjectMapper $$mapper = createObjectMapper()

  private String $name = ""
  private List<PseudoWord> $pseudoWords = ArrayList.new()

  public static EquivalentCollection load(EquivalentCollectionType type) {
    File file = File.new(type.getPath())
    if (file.exists()) {
      FileInputStream stream = FileInputStream.new(file)
      EquivalentCollection collection
      try {
        collection = $$mapper.readValue(stream, EquivalentCollection)
      } catch (Exception exception) {
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

  public static ObjectMapper getObjectMapper() {
    return $$mapper
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