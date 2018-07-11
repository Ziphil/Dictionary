package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import ziphil.custom.Measurement
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public enum BadgeType {

  CIRCLE("マーク1", "circle-badge", "circle"),
  SQUARE("マーク2", "square-badge", "square"),
  UP_TRIANGLE("マーク3", "up-triangle-badge", "up_triangle"),
  DOWN_TRIANGLE("マーク4", "down-triangle-badge", "down_triangle")

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

  public static Node createImageNode() {
    HBox box = HBox.new(Measurement.rpx(3))
    box.getStyleClass().add("badge-container")
    for (BadgeType type : BadgeType.values()) {
      ImageView view = ImageView.new(type.createImage())
      view.getStyleClass().add(type.getStyleClass())
      box.getChildren().add(view)
    }
    return box
  }

  public String getName() {
    return $name
  }

  public String getStyleClass() {
    return $styleClass
  }

}