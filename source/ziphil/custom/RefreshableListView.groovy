package ziphil.custom

import groovy.transform.CompileStatic
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
    Skin<?> skin = getSkin()
    if (skin instanceof RefreshableListViewSkin) {
      skin.refresh()
    }
  }

  protected Skin<RefreshableListView<T>> createDefaultSkin() {
    return RefreshableListViewSkin.new(this)
  }

}