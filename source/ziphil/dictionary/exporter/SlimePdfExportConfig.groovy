package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfExportConfig extends PdfExportConfig {

  private String $firstCaptionFontFamily = "Yu Gothic"
  private String $secondCaptionFontFamily = "sans-serif"
  private Int $captionFontSize = 20
  private String $firstHeadFontFamily = "Yu Gothic"
  private String $secondHeadFontFamily = "sans-serif"
  private Int $headFontSize = 10
  private String $firstMainFontFamily = "Yu Mincho"
  private String $secondMainFontFamily = "serif"
  private Int $mainFontSize = 8
  private String $variationMarker = null
  private String $relationMarker = null
  private Boolean $modifies = true
  private String $externalCommand = null

  public String getFirstCaptionFontFamily() {
    return $firstCaptionFontFamily
  }

  public void setFirstCaptionFontFamily(String firstCaptionFontFamily) {
    $firstCaptionFontFamily = firstCaptionFontFamily
  }

  public String getSecondCaptionFontFamily() {
    return $secondCaptionFontFamily
  }

  public void setSecondCaptionFontFamily(String secondCaptionFontFamily) {
    $secondCaptionFontFamily = secondCaptionFontFamily
  }

  public Int getCaptionFontSize() {
    return $captionFontSize
  }

  public void setCaptionFontSize(Int captionFontSize) {
    $captionFontSize = captionFontSize
  }

  public String getFirstHeadFontFamily() {
    return $firstHeadFontFamily
  }

  public void setFirstHeadFontFamily(String firstHeadFontFamily) {
    $firstHeadFontFamily = firstHeadFontFamily
  }

  public String getSecondHeadFontFamily() {
    return $secondHeadFontFamily
  }

  public void setSecondHeadFontFamily(String secondHeadFontFamily) {
    $secondHeadFontFamily = secondHeadFontFamily
  }

  public Int getHeadFontSize() {
    return $headFontSize
  }

  public void setHeadFontSize(Int headFontSize) {
    $headFontSize = headFontSize
  }

  public String getFirstMainFontFamily() {
    return $firstMainFontFamily
  }

  public void setFirstMainFontFamily(String firstMainFontFamily) {
    $firstMainFontFamily = firstMainFontFamily
  }

  public String getSecondMainFontFamily() {
    return $secondMainFontFamily
  }

  public void setSecondMainFontFamily(String secondMainFontFamily) {
    $secondMainFontFamily = secondMainFontFamily
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