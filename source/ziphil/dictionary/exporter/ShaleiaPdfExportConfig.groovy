package ziphil.dictionary.exporter

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaPdfExportConfig extends PdfExportConfig {

  private String $firstCaptionFontFamily = "FreeSans"
  private String $secondCaptionFontFamily = "源ノ角ゴシック"
  private Double $captionFontSize = 20
  private String $firstHeadFontFamily = "FreeSans"
  private String $secondHeadFontFamily = "源ノ角ゴシック"
  private Double $headFontSize = 10
  private String $firstShaleiaFontFamily = "FreeSans"
  private String $secondShaleiaFontFamily = "源ノ角ゴシック"
  private Double $shaleiaFontSize = 7.6
  private String $firstMainFontFamily = "Linux Libertine"
  private String $secondMainFontFamily = "源ノ明朝"
  private Double $mainFontSize = 8
  private String $relationMarker = null
  private Boolean $modifies = true

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

  public Double getCaptionFontSize() {
    return $captionFontSize
  }

  public void setCaptionFontSize(Double captionFontSize) {
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

  public Double getHeadFontSize() {
    return $headFontSize
  }

  public void setHeadFontSize(Double headFontSize) {
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

  public Double getShaleiaFontSize() {
    return $shaleiaFontSize
  }

  public void setShaleiaFontSize(Double shaleiaFontSize) {
    $shaleiaFontSize = shaleiaFontSize
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

  public Double getMainFontSize() {
    return $mainFontSize
  }

  public void setMainFontSize(Double mainFontSize) {
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

}