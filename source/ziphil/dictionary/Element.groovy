package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane


@CompileStatic
public interface Element {

  public void update()

  public void updateContentPane()

  public Pane getContentPane()

}