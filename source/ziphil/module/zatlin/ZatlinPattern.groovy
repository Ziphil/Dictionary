package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ZatlinPattern implements ZatlinMatchable {

  private ZatlinToken $token
  private Boolean $leading = false
  private Boolean $trailing = false

  public String toString() {
    StringBuilder string = StringBuilder.new()
    if ($leading) {
      string.append("^ ")
    }
    string.append($token)
    if ($trailing) {
      string.append(" ^")
    }
    return string.toString()
  }

  public Boolean hasToken() {
    return $token != null
  }

  public ZatlinToken getToken() {
    return $token
  }

  public void setToken(ZatlinToken token) {
    $token = token
  }

  public Boolean isLeading() {
    return $leading
  }

  public void setLeading(Boolean leading) {
    $leading = leading
  }

  public Boolean isTrailing() {
    return $trailing
  }

  public void setTrailing(Boolean trailing) {
    $trailing = trailing
  }

}