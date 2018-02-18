package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphil.dictionary.ExportConfig
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfExportConfig extends ExportConfig {

  private String $captionFontFamily = "sans-serif"
  private Int $captionFontSize = 20
  private String $headFontFamily = "sans-serif"
  private Int $headFontSize = 10
  private String $mainFontFamily = "serif"
  private Int $mainFontSize = 8
  private String $variationMarker = null
  private String $relationMarker = null
  private Boolean $modifies = true
  private String $externalCommand = null

  public String getCaptionFontFamily() {
    return $captionFontFamily
  }

  public void setCaptionFontFamily(String captionFontFamily) {
    $captionFontFamily = captionFontFamily
  }

  public Int getCaptionFontSize() {
    return $captionFontSize
  }

  public void setCaptionFontSize(Int captionFontSize) {
    $captionFontSize = captionFontSize
  }

  public String getHeadFontFamily() {
    return $headFontFamily
  }

  public void setHeadFontFamily(String headFontFamily) {
    $headFontFamily = headFontFamily
  }

  public Int getHeadFontSize() {
    return $headFontSize
  }

  public void setHeadFontSize(Int headFontSize) {
    $headFontSize = headFontSize
  }

  public String getMainFontFamily() {
    return $mainFontFamily
  }

  public void setMainFontFamily(String mainFontFamily) {
    $mainFontFamily = mainFontFamily
  }

  public Int getMainFontSize() {
    return $mainFontSize
  }

  public void setMainFontSize(Int mainFontSize) {
    $mainFontSize = mainFontSize
  }

  public String getVariationMarker() {
    return $variationMarker
  }

  public void setVariationMarker(String variationMarker) {
    $variationMarker = variationMarker
  }

  public String getRelationMarker() {
    return $relationMarker
  }

  public void setRelationMarker(String relationMarker) {
    $relationMarker = relationMarker
  }

  public Boolean getModifies() {
    return $modifies
  }

  public void setModifies(Boolean modifies) {
    $modifies = modifies
  }

  public String getExternalCommand() {
    return $externalCommand
  }

  public void setExternalCommand(String externalCommand) {
    $externalCommand = externalCommand
  }

}