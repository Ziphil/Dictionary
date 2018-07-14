package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane


@CompileStatic
public interface Word extends Element {

  public String getName()

  public List<String> getEquivalents()

  public String getContent()

  public String getIdentifier()

  public PaneFactory<Pane> getPlainPaneFactory()

}