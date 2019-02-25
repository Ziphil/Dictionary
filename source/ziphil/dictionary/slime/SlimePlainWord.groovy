package ziphil.dictionary.slime

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePlainWord {

  private Int $number = -1
  private String $name = ""
  private List<SlimeEquivalent> $equivalents = ArrayList.new()
  private List<String> $tags = ArrayList.new()
  private List<SlimeInformation> $informations = ArrayList.new()
  private List<SlimeVariation> $variations = ArrayList.new()
  private List<SlimePlainRelation> $relations = ArrayList.new()

  public Int getNumber() {
    return $number
  }

  public void setNumber(Int number) {
    $number = number
  }

  public String getName() {
    return $name
  }

  public void setName(String name) {
    $name = name
  }

  public List<SlimeEquivalent> getEquivalents() {
    return $equivalents
  }

  public void setEquivalents(List<SlimeEquivalent> equivalents) {
    $equivalents = equivalents
  }

  public List<String> getTags() {
    return $tags
  }

  public void setTags(List<String> tags) {
    $tags = tags
  }

  public List<SlimeInformation> getInformations() {
    return $informations
  }

  public void setInformations(List<SlimeInformation> informations) {
    $informations = informations
  }

  public List<SlimeVariation> getVariations() {
    return $variations
  }

  public void setVariations(List<SlimeVariation> variations) {
    $variations = variations
  }

  public List<SlimePlainRelation> getRelations() {
    return $relations
  }

  public void setRelations(List<SlimePlainRelation> relations) {
    $relations = relations
  }

}