package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimBoolean
import ziphilib.type.PrimChar
import ziphilib.type.PrimInt
import ziphilib.type.PrimLong


@CompileStatic @Ziphilify
public class ExtendedBufferedReader extends BufferedReader {

  private PrimInt $lineNumber = 1
  private PrimInt $columnNumber = 0
  private PrimBoolean $skipsLineFeed = false
  private PrimBoolean $passesLineFeed = false
  private PrimInt $markedLineNumber = 1
  private PrimInt $markedColumnNumber = 0
  private PrimBoolean $markedSkipsLineFeed = false
  private PrimBoolean $markedPassesLineFeed = false

  public ExtendedBufferedReader(Reader reader) {
    super(reader)
  }

  public PrimInt read() {
    synchronized (this.@lock) {
      PrimInt result = super.read()
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
        result = (PrimChar)'\n'
      } else if (result == '\n') {
        $passesLineFeed = true
      }
      return result
    }
  }

  public PrimInt read(PrimChar[] buffer, PrimInt offset, PrimInt length) {
    synchronized (this.@lock) {
      PrimInt size = super.read(buffer, offset, length)
      for (PrimInt i = offset ; i < offset + size ; i ++) {
        PrimInt character = buffer[i]
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

  public void mark(PrimInt readAheadLimit) {
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

  public PrimInt getLineNumber() {
    return $lineNumber
  }

  public PrimInt getColumnNumber() {
    return $columnNumber
  }

}