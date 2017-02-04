package ziphil.dictionary.database

import groovy.transform.CompileStatic
import java.sql.PreparedStatement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class StatementGroup {

  private PreparedStatement $entryStatement
  private PreparedStatement $equivalentStatement
  private PreparedStatement $equivalentNameStatement
  private PreparedStatement $tagStatement
  private PreparedStatement $informationStatement
  private PreparedStatement $variationStatement
  private PreparedStatement $relationStatement

  public void close() {
    $entryStatement.close()
    $equivalentStatement.close()
    $equivalentNameStatement.close()
    $tagStatement.close()
    $informationStatement.close()
    $variationStatement.close()
    $relationStatement.close()
  }

  public PreparedStatement getEntryStatement() {
    return $entryStatement
  }

  public void setEntryStatement(PreparedStatement entryStatement) {
    $entryStatement = entryStatement
  }

  public PreparedStatement getEquivalentStatement() {
    return $equivalentStatement
  }

  public void setEquivalentStatement(PreparedStatement equivalentStatement) {
    $equivalentStatement = equivalentStatement
  }

  public PreparedStatement getEquivalentNameStatement() {
    return $equivalentNameStatement
  }

  public void setEquivalentNameStatement(PreparedStatement equivalentNameStatement) {
    $equivalentNameStatement = equivalentNameStatement
  }

  public PreparedStatement getTagStatement() {
    return $tagStatement
  }

  public void setTagStatement(PreparedStatement tagStatement) {
    $tagStatement = tagStatement
  }

  public PreparedStatement getInformationStatement() {
    return $informationStatement
  }

  public void setInformationStatement(PreparedStatement informationStatement) {
    $informationStatement = informationStatement
  }

  public PreparedStatement getVariationStatement() {
    return $variationStatement
  }

  public void setVariationStatement(PreparedStatement variationStatement) {
    $variationStatement = variationStatement
  }

  public PreparedStatement getRelationStatement() {
    return $relationStatement
  }

  public void setRelationStatement(PreparedStatement relationStatement) {
    $relationStatement = relationStatement
  }


}