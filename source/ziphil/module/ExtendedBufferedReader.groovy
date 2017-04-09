package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitives
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean
import ziphilib.type.PrimCharacter
import ziphilib.type.PrimInteger
import ziphilib.type.PrimLong


@CompileStatic @Ziphilify @ConvertPrimitives
public class ExtendedBufferedReader extends BufferedReader {

  private PrimInteger $lineNumber = 1
  private PrimInteger $columnNumber = 0
  private PrimBoolean $skipsLineFeed = false
  private PrimInteger $markedLineNumber = 1
  private PrimInteger $markedColumnNumber = 0
  private PrimBoolean $markedSkipsLineFeed = false

  public ExtendedBufferedReader(Reader reader) {
    super(reader)
  }

  public PrimInteger read() {
    synchronized (this.@lock) {
      PrimInteger result = super.read()
      $columnNumber ++
      if ($skipsLineFeed) {
        $skipsLineFeed = false
        if (result == '\n') {
          result = super.read()
          $columnNumber ++
        }
      }
      if (result == '\r') {
        $lineNumber ++
        $columnNumber = 0
        $skipsLineFeed = true
        result = 10
      } else if (result == '\n') {
        $lineNumber ++
        $columnNumber = 0
      }
      return result
    }
  }

  public PrimInteger read(PrimCharacter[] buffer, PrimInteger offset, PrimInteger length) {
    synchronized (this.@lock) {
      PrimInteger size = super.read(buffer, offset, length)
      for (PrimInteger i = offset ; i < offset + size ; i ++) {
        PrimInteger character = buffer[i]
        $columnNumber ++
        if ($skipsLineFeed) {
          $skipsLineFeed = false
          if (character == '\n') {
            continue
          }
        }
        if (character == '\r') {
          $lineNumber ++
          $columnNumber = 0
          $skipsLineFeed = true
          break
        } else {
          $lineNumber ++
          $columnNumber = 0
          break
        }
      }
      return size
    }
  }

  public String readLine() {
    synchronized (this.@lock) {
      String line = super.readLine($skipsLineFeed)
      $skipsLineFeed = false
      if (line != null) {
        $lineNumber ++
        $columnNumber = 0
      }
      return line
    }
  }

  public PrimLong skip(PrimLong size) {
    throw UnsupportedOperationException.new()
  }

  public void mark(PrimInteger readAheadLimit) {
    synchronized (this.@lock) {
      super.mark(readAheadLimit)
      $markedLineNumber = $lineNumber
      $markedColumnNumber = $columnNumber
      $markedSkipsLineFeed = $skipsLineFeed
    }
  }

  public void reset() {
    synchronized (this.@lock) {
      super.reset()
      $lineNumber = $markedLineNumber
      $columnNumber = $markedColumnNumber
      $skipsLineFeed = $markedSkipsLineFeed
    }
  }

  public PrimInteger getLineNumber() {
    return $lineNumber
  }

  public PrimInteger getColumnNumber() {
    return $columnNumber
  }

}