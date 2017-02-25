package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.scene.layout.Pane


@CompileStatic
public interface ContentPaneFactory {

  public static final String CONTENT_CLASS = "content"
  public static final String CONTENT_PANE_CLASS = "content-pane"
  public static final String HEAD_NAME_CLASS = "head-name"

  public Pane create()

}