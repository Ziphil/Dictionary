package ziphil.custom

import groovy.transform.CompileStatic
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ziphil.dictionary.Badge
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BadgeCell extends ListCell<Badge> {

  public BadgeCell() {
    super()
  }

  protected void updateItem(Badge badge, Boolean empty) {
    super.updateItem(badge, empty)
    if (empty || badge == null) {
      setText(null)
      setGraphic(null)
    } else {
      Image image = badge.getImage()
      setText(badge.toString())
      setGraphic(ImageView.new(image))
    }
  }

}