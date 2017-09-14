package ziphil.custom

import groovy.transform.CompileStatic
import javafx.event.Event
import javafx.scene.control.Tab
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ClosableTab extends Tab {

  public void requestClose() {
    Event event = Event.new(TAB_CLOSE_REQUEST_EVENT)
    Event.fireEvent(this, event)
    if (!event.isConsumed()) {
      getTabPane().getTabs().remove(this)
      event.consume()
    }
  }

}