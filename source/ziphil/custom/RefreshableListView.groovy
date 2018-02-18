package ziphil.custom

import groovy.transform.CompileStatic
import java.lang.reflect.Field
import java.lang.reflect.Method
import javafx.collections.ObservableList
import javafx.scene.control.ListView
import javafx.scene.control.Skin
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class RefreshableListView<T> extends ListView<T> {

  public RefreshableListView() {
    super()
  }

  public RefreshableListView(ObservableList<T> items) {
    super(items)
  }

  public void refresh() {
    try {
      Skin<?> skin = getSkin()
      Field flowField = skin.getClass().getSuperclass().getDeclaredField("flow")
      flowField.setAccessible(true)
      Object flow = flowField.get(skin)
      Method method = flow.getClass().getMethod("rebuildCells")
      method.invoke(flow)
    } catch (Exception exception) {
    }
  }

}