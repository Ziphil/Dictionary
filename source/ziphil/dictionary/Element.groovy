package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane


@CompileStatic
public interface Element {

  public void createContentPane()

  public void update()

  public void change()

  public Pane getContentPane()

  public Boolean isChanged()

}