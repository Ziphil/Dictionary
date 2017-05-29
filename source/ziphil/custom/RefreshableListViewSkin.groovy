package ziphil.custom

import com.sun.javafx.scene.control.skin.ListViewSkin
import groovy.transform.CompileStatic
import javafx.scene.control.ListView
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class RefreshableListViewSkin<T> extends ListViewSkin<T> {

  public RefreshableListViewSkin(RefreshableListView<T> control) {
    super(control)
  }
  
  public void refresh() {
    this.@flow.rebuildCells()
  }

}