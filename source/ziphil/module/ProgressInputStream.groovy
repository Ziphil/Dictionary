package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitive


@CompileStatic @Newify
public class ProgressInputStream extends InputStream {

  private InputStream $stream
  private Integer $readSize = 0

  public ProgressInputStream(InputStream stream) {
    $stream = stream
  }

  @ConvertPrimitive
  public Integer read() {
    Integer size = $stream.read()
    if (size != -1) {
      $readSize ++
    }
    return size
  }

  @ConvertPrimitive
  public Integer read(Byte[] bytes) {
    Integer size = $stream.read(bytes)
    $readSize += size
    return size
  }

  @ConvertPrimitive
  public Integer read(Byte[] bytes, Integer offset, Integer length) {
    Integer size = $stream.read(bytes, offset, length)
    $readSize += size
    return size
  }

  public Integer getReadSize() {
    return $readSize
  }

}