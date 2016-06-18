package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.text.Font
import javafx.util.Builder


@CompileStatic @Newify
public class Measurement implements Builder<Double> {

  private static final Double UNIT_REM = calculateUnitRem()
  private static final Double UNIT_RPX = calculateUnitRpx()
  private static final Double OVERRIDEN_FONT_SIZE = null

  private Double $value = 0
  private String $unit

  public Double build() {
    if ($unit == "rem") {
      return $value * UNIT_REM
    } else if ($unit == "rpx") {
      return $value * UNIT_RPX
    } else {
      return $value
    }
  }

  public String getRem() {
    return null
  }

  public void setRem(String value) {
    $value = value.toDouble()
    $unit = "rem"
  }

  public static Double rem(Integer value) {
    return value * UNIT_REM
  }

  public String getRpx() {
    return null
  }

  public void setRpx(String value) {
    $value = value.toDouble()
    $unit = "rpx"
  }

  public static Double rpx(Integer value) {
    return value * UNIT_RPX
  }

  private static Double calculateUnitRem() {
    Double fontSize = OVERRIDEN_FONT_SIZE ?: Font.getDefault().getSize()
    return fontSize
  }

  private static Double calculateUnitRpx() {
    Double fontSize = OVERRIDEN_FONT_SIZE ?: Font.getDefault().getSize()
    return (Double)(fontSize / 12)
  }
  
}