package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitives


@CompileStatic @ConvertPrimitives
public interface NameGenerator {

  public String generate()

}