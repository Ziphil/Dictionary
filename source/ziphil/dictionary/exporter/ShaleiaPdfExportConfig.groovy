package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPdfExportConfig extends PdfExportConfig {

  private String $firstCaptionFontFamily = "sans-serif"
  private String $secondCaptionFontFamily = "sans-serif"
  private Int $captionFontSize = 20
  private String $firstHeadFontFamily = "sans-serif"
  private String $secondHeadFontFamily = "sans-serif"
  private Int $headFontSize = 10
  private String $firstShaleiaFontFamily = "serif"
  private String $secondShaleiaFontFamily = "serif"
  private String $firstMainFontFamily = "serif"
  private String $secondMainFontFamily = "serif"
  private Int $mainFontSize = 8
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

  public String getFirstShaleiaFontFamily() {
    return $firstShaleiaFontFamily
  }

  public void setFirstShaleiaFontFamily(String firstShaleiaFontFamily) {
    $firstShaleiaFontFamily = firstShaleiaFontFamily
  }

  public String getSecondShaleiaFontFamily() {
    return $secondShaleiaFontFamily
  }

  public void setSecondShaleiaFontFamily(String secondShaleiaFontFamily) {
    $secondShaleiaFontFamily = secondShaleiaFontFamily
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