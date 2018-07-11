package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane


@CompileStatic
public interface Word extends Element {

  public String getName()

  public void setName(String name)

  public List<String> getEquivalents()

  public void setEquivalents(List<String> equivalents)

  public String getContent()

  public void setContent(String content)

  public String getIdentifier()

  public PaneFactory getPlainPaneFactory()

}