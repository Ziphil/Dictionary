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

  CIRCLE("マーク1", "circle"),
  SQUARE("マーク2", "square"),
  UP_TRIANGLE("マーク3", "up_triangle"),
  DOWN_TRIANGLE("マーク4", "down_triangle")

  private static final String IMAGE_DIRECTORY = "resource/image/badge/"

  private String $name
  private String $path

  private BadgeType(String name, String path) {
    $name = name
    $path = path
  }

  public Image createImage() {
    return Image.new(getClass().getClassLoader().getResourceAsStream(IMAGE_DIRECTORY + $path + ".png"))
  }

  public static Node createImageNode() {
    HBox box = HBox.new(Measurement.rpx(3))
    for (BadgeType type : BadgeType.values()) {
      ImageView view = ImageView.new(type.createImage())
      box.getChildren().add(view)
    }
    return box
  }

}