package ziphil.custom

import groovy.transform.CompileStatic
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CustomSkinBase<C extends Control, N extends Node> extends SkinBase<C> {

  protected N $node
  protected C $control

  public CustomSkinBase(C control) {
    super(control)
    $control = control
  }

  protected void setupNode() {
    getChildren().add($node)
  }

  protected void loadResource(String resourcePath) {
    FXMLLoader loader = FXMLLoader.new(getClass().getClassLoader().getResource(resourcePath), null, CustomBuilderFactory.new())
    loader.setRoot($node)
    loader.setController(this)
    loader.load()
  }

}