package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ExtendedBufferedReader extends BufferedReader {

  private Int $lineNumber = 1
  private Int $columnNumber = 0
  private Boolean $skipsLineFeed = false
  private Boolean $passesLineFeed = false
  private Int $markedLineNumber = 1
  private Int $markedColumnNumber = 0
  private Boolean $markedSkipsLineFeed = false
  private Boolean $markedPassesLineFeed = false

  public ExtendedBufferedReader(Reader reader) {
    super(reader)
  }

  public Int read() {
    synchronized (this.@lock) {
      Int result = super.read()
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
        result = (Char)'\n'
      } else if (result == '\n') {
        $passesLineFeed = true
      }
      return result
    }
  }

  public Int read(Char[] buffer, Int offset, Int length) {
    synchronized (this.@lock) {
      Int size = super.read(buffer, offset, length)
      for (Int i = offset ; i < offset + size ; i ++) {
        Int character = buffer[i]
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

  public Long skip(Long size) {
    throw UnsupportedOperationException.new()
  }

  public void mark(Int readAheadLimit) {
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

  public Int getLineNumber() {
    return $lineNumber
  }

  public Int getColumnNumber() {
    return $columnNumber
  }

}