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
  private PrimBoolean $passesLineFeed = false
  private PrimInteger $markedLineNumber = 1
  private PrimInteger $markedColumnNumber = 0
  private PrimBoolean $markedSkipsLineFeed = false
  private PrimBoolean $markedPassesLineFeed = false

  public ExtendedBufferedReader(Reader reader) {
    super(reader)
  }

  public PrimInteger read() {
    synchronized (this.@lock) {
      PrimInteger result = super.read()
      if ($passesLineFeed) {
        $lineNumber ++
        $columnNumber = 0
        $passesLineFeed = false
      }
      $columnNumber ++
      if ($skipsLineFeed) {
        $skipsLineFeed = false
        if (result == '\n') {
          result = super.read()
          $columnNumber ++
        }
      }
      if (result == '\r') {
        $skipsLineFeed = true
        $passesLineFeed = true
        result = (PrimCharacter)'n'
      } else if (result == '\n') {
        $passesLineFeed = true
      }
      return result
    }
  }

  public PrimInteger read(PrimCharacter[] buffer, PrimInteger offset, PrimInteger length) {
    synchronized (this.@lock) {
      PrimInteger size = super.read(buffer, offset, length)
      for (PrimInteger i = offset ; i < offset + size ; i ++) {
        PrimInteger character = buffer[i]
        if ($passesLineFeed) {
          $lineNumber ++
          $columnNumber = 0
          $passesLineFeed = false
        }
        $columnNumber ++
        if ($skipsLineFeed) {
          $skipsLineFeed = false
          if (character == '\n') {
            continue
          }
        }
        if (character == '\r') {
          $skipsLineFeed = true
          $passesLineFeed = true
          break
        } else {
          $passesLineFeed = true
          break
        }
      }
      return size
    }
  }

  public String readLine() {
    synchronized (this.@lock) {
      String line = super.readLine($skipsLineFeed)
      if ($passesLineFeed) {
        $lineNumber ++
        $columnNumber = 0
        $passesLineFeed = false
      }
      if (line != null) {
        $columnNumber += line.length()
        $passesLineFeed = true
      }
      $skipsLineFeed = false
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
      $markedPassesLineFeed = $passesLineFeed
    }
  }

  public void reset() {
    synchronized (this.@lock) {
      super.reset()
      $lineNumber = $markedLineNumber
      $columnNumber = $markedColumnNumber
      $skipsLineFeed = $markedSkipsLineFeed
      $passesLineFeed = $markedPassesLineFeed
    }
  }

  public PrimInteger getLineNumber() {
    return $lineNumber
  }

  public PrimInteger getColumnNumber() {
    return $columnNumber
  }

}