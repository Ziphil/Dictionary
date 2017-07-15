package ziphil.module.zatlin

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface ZatlinMatchable {

  public Boolean match(String input, ZatlinRoot root)

}