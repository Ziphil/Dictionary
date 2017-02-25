package ziphil.custom

import com.sun.javafx.scene.control.skin.ListViewSkin
import groovy.transform.CompileStatic
import javafx.scene.control.ListView
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class UpdatableListViewSkin<T> extends ListViewSkin<T> {

  public UpdatableListViewSkin(ListView<T> control) {
    super(control)
  }
  
  public void refresh() {
    this.@flow.rebuildCells()
  }

}