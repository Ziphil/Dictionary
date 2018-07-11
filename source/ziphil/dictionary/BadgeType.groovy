package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.image.Image
import ziphil.custom.Measurement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum BadgeType {

  CIRCLE("マーク1", "circle-badge", "circle"),
  SQUARE("マーク2", "square-badge", "square"),
  UP_TRIANGLE("マーク3", "up-triangle-badge", "up_triangle"),
  DIAMOND("マーク4", "diamond-badge", "diamond"),
  DOWN_TRIANGLE("マーク5", "down-triangle-badge", "down_triangle"),
  CROSS("マーク6", "cross-badge", "cross"),
  DROP("マーク7", "drop-badge", "drop"),
  HEART("マーク8", "heart-badge", "heart")

  private static final String IMAGE_DIRECTORY = "resource/image/badge/"

  private String $name
  private String $styleClass
  private String $path

  private BadgeType(String name, String styleClass, String path) {
    $name = name
    $styleClass = styleClass
    $path = path
  }

  public Image createImage() {
    return Image.new(getClass().getClassLoader().getResourceAsStream(IMAGE_DIRECTORY + $path + ".png"))
  }

  public String getName() {
    return $name
  }

  public String getStyleClass() {
    return $styleClass
  }

}