package ziphil.module.akrantiain

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AkrantiainLexer implements Closeable, AutoCloseable {

  private Reader $reader
  private String $version

  public AkrantiainLexer(Reader reader, String version) {
    $reader = reader
    $version = version
    checkReader()
  }

  private void checkReader() {
    if (!$reader.markSupported()) {
      throw IllegalArgumentException.new("Reader does not support the mark operation")
    }
  }

  public AkrantiainToken nextToken() {
    return null
  }

  public void close() {
    $reader.close()
  }

}