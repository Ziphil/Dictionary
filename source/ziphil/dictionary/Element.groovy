package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphil.custom.ElementPane


@CompileStatic
public interface Element {

  public void update()

  public PaneFactory<ElementPane> getPaneFactory()

}