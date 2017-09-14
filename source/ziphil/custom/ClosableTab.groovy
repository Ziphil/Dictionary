package ziphil.custom

import groovy.transform.CompileStatic
import javafx.event.Event
import javafx.scene.control.Tab
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ClosableTab extends Tab {

  public void close() {
    Event.fireEvent(this, Event.new(TAB_CLOSE_REQUEST_EVENT))
    getTabPane().getTabs().remove(this)
  }

}