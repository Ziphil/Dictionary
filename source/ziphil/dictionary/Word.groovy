package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane


@CompileStatic @Newify
public abstract class Word {

  public static final String CONTENT_CLASS = "content"
  public static final String HEAD_NAME_CLASS = "head-name"

  public abstract void createContentPane()

  public abstract Boolean isChanged()

  public abstract String getName()

  public abstract List<String> getEquivalents()

  public abstract String getContent()

  public abstract Pane getContentPane()

}