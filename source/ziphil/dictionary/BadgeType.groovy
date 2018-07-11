package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.image.Image
import ziphil.custom.Measurement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum BadgeType {

  CIRCLE("マーカー1", "circle-badge", "circle"),
  SQUARE("マーカー2", "square-badge", "square"),
  UP_TRIANGLE("マーカー3", "up-triangle-badge", "up_triangle"),
  DIAMOND("マーカー4", "diamond-badge", "diamond"),
  DOWN_TRIANGLE("マーカー5", "down-triangle-badge", "down_triangle"),
  CROSS("マーカー6", "cross-badge", "cross"),
  DROP("マーカー7", "drop-badge", "drop"),
  HEART("マーカー8", "heart-badge", "heart")

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