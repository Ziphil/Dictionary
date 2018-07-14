package ziphil.dictionary

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface PaneFactory<P> {

  public static final String CONTENT_CLASS = "content"
  public static final String CONTENT_PANE_CLASS = "content-pane"
  public static final String HEAD_NAME_CLASS = "head-name"
  public static final String SMALL_CLASS = "small"
  public static final String SEPARATOR_CLASS = "separator"
  public static final String MARGIN_CLASS = "margin"

  // 表示用のペインを生成します。
  // ペインがすでに生成されてそれが保持されている場合は、新たにペインを生成することはせずに、保持されているペインをそのまま返します。
  // ただし、forcesCreate に true を指定すると、すでに生成済みのペインが保持されているかどうかに関わらず新たにペインを生成します。
  public P create(Boolean forcesCreate)

  public void destroy()

  // ペイン生成時のソースとなる単語オブジェクトが変更されたことを、このオブジェクトに伝えます。
  // このメソッドが呼び出され直後は、create メソッドは常に新たにペインを生成します。
  public void change()

  // 生成したペインをフィールドに保持するかどうかを指定します。
  // true を渡すとペインを保持するようになり、false を渡すと create メソッドが呼ばれるたびに新たにペインを生成するようになります。
  public void setPersisted(Boolean persisted)

}