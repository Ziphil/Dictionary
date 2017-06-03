package ziphil.custom

import groovy.transform.CompileStatic
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.stage.Stage
import javafx.stage.StageStyle
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Dialog extends Stage {

  private StringProperty $contentText = SimpleStringProperty.new("")
  private StringProperty $commitText = SimpleStringProperty.new("OK")
  private StringProperty $negateText = SimpleStringProperty.new("NG")
  private StringProperty $cancelText = SimpleStringProperty.new("キャンセル")
  private BooleanProperty $allowsNegate = SimpleBooleanProperty.new(false)
  private BooleanProperty $allowsCancel = SimpleBooleanProperty.new(true)
  private Status $status = Status.CANCELLED

  public Dialog(StageStyle style) {
    super(style)
    makeManager()
  }

  private void makeManager() {
    DialogManager manager = DialogManager.new(this)
  }

  public void commit() {
    $status = Status.COMMITTED
  }

  public void negate() {
    $status = Status.NEGATED
  }

  public void cancel() {
    $status = Status.CANCELLED
  }

  public Boolean isCommitted() {
    return $status == Status.COMMITTED
  }

  public Boolean isNegated() {
    return $status == Status.NEGATED
  }

  public Boolean isCancelled() {
    return $status == Status.CANCELLED
  }

  public String getContentText() {
    return $contentText.get()
  }

  public void setContentText(String contentText) {
    $contentText.set(contentText)
  }

  public StringProperty contentTextProperty() {
    return $contentText
  }

  public String getCommitText() {
    return $commitText.get()
  }

  public void setCommitText(String commitText) {
    $commitText.set(commitText)
  }

  public StringProperty commitTextProperty() {
    return $commitText
  }

  public String getNegateText() {
    return $negateText.get()
  }

  public void setNegateText(String negateText) {
    $negateText.set(negateText)
  }

  public StringProperty negateTextProperty() {
    return $negateText
  }

  public String getCancelText() {
    return $cancelText.get()
  }

  public void setCancelText(String cancelText) {
    $cancelText.set(cancelText)
  }

  public StringProperty cancelTextProperty() {
    return $cancelText
  }

  public Boolean isAllowsNegate() {
    return $allowsNegate.get()
  }

  public void setAllowsNegate(Boolean allowsNegate) {
    $allowsNegate.set(allowsNegate)
  }

  public BooleanProperty allowsNegateProperty() {
    return $allowsNegate
  }

  public Boolean isAllowsCancel() {
    return $allowsCancel.get()
  }

  public void setAllowsCancel(Boolean allowsCancel) {
    $allowsCancel.set(allowsCancel)
  }

  public BooleanProperty allowsCancelProperty() {
    return $allowsCancel
  }

}


@InnerClass(Dialog)
@Ziphilify
private static enum Status {

  COMMITTED,
  NEGATED,
  CANCELLED

}