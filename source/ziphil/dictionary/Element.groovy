package ziphil.dictionary

import groovy.transform.CompileStatic


@CompileStatic
public interface Element {

  public void update()

  public ContentPaneFactory getContentPaneFactory()

}