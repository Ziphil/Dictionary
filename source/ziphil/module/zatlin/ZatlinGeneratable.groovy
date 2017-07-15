package ziphil.module.zatlin

import groovy.transform.CompileStatic


@CompileStatic
public interface ZatlinGeneratable {

  public String generate(ZatlinRoot root)

}