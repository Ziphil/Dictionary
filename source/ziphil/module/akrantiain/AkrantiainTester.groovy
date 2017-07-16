package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainTester {

  public static void measure(String path, Int size, Int stepSize) {
    Akrantiain akrantiain = Akrantiain.new()
    StringBuilder input = StringBuilder.new()
    for (Int i = 0 ; i < size ; i ++) {
      input.append("z")
    }
    akrantiain.load(File.new(path))
    Long totalElapsedTime = 0
    for (Int i = 0 ; i < stepSize ; i ++) {
      Long beforeTime = System.nanoTime()
      akrantiain.convert(input.toString())
      Long afterTime = System.nanoTime()
      Long elapsedTime = afterTime - beforeTime
      totalElapsedTime += elapsedTime
    }
    printf("AVG: %.3f\n", totalElapsedTime / stepSize)
  }

}