package ziphil.dictionary

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import groovy.transform.CompileStatic
import ziphil.Launcher
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class EquivalentCollectionType {

  private static final String COLLECTION_DIRECTORY = "data/collection/"

  private String $name
  private String $path
  private Int $size

  private EquivalentCollectionType(String name, String path, Int size) {
    $name = name
    $path = path
    $size = size
  }

  public static List<EquivalentCollectionType> getCollectionTypes() {
    File directory = File.new(Launcher.BASE_PATH + COLLECTION_DIRECTORY)
    List<EquivalentCollectionType> collectionTypes = ArrayList.new()
    for (File file : directory.listFiles()) {
      if (file.isFile()) {
        FileInputStream stream = FileInputStream.new(file)
        JsonFactory factory = EquivalentCollection.getObjectMapper().getFactory()
        JsonParser parser = factory.createParser(stream)
        String name = ""
        Int size = 0
        try {
          parser.nextToken()
          while (parser.nextToken() == JsonToken.FIELD_NAME) {
            String fieldName = parser.getCurrentName()
            parser.nextToken()
            if (fieldName == "name") {
              name = parser.getValueAsString()
            } else if (fieldName == "data") {
              while (parser.nextToken() != JsonToken.END_ARRAY) {
                parser.skipChildren()
                size ++
              }
            }
          }
          EquivalentCollectionType collectionType = EquivalentCollectionType.new(name, file.getAbsolutePath(), size)
          collectionTypes.add(collectionType)
        } catch (Exception exception) {
        } finally {
          parser.close()
          stream.close()
        }
      }
    }
    collectionTypes.sort() { EquivalentCollectionType firstCollectionType, EquivalentCollectionType secondCollectionType ->
      return firstCollectionType.getSize() <=> secondCollectionType.getSize()
    }
    return collectionTypes
  }

  public String toString() {
    return "${$name} (${$size}語)"
  }

  public String getName() {
    return $name
  }

  public String getPath() {
    return $path
  }

  public Int getSize() {
    return $size
  }

}