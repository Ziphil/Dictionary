package ziphil.module

import groovy.transform.CompileStatic
import java.nio.charset.MalformedInputException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BocuDecodableInputStream extends BufferedInputStream {

  private static final Int ASCII_PREVIOUS = 0x40
  private static final Int MIN = 0x21
  private static final Int MIDDLE = 0x90
  private static final Int MAX_LEAD = 0xFE
  private static final Int MAX_TRAIL = 0xFF
  private static final Int RESET = 0xFF
  private static final Int COUNT = MAX_LEAD - MIN + 1
  private static final Int TRAIL_CONTROL_COUNT = 20
  private static final Int TRAIL_BYTE_OFFSET = MIN - TRAIL_CONTROL_COUNT
  private static final Int TRAIL_COUNT = MAX_TRAIL - MIN + TRAIL_CONTROL_COUNT + 1
  private static final Int SINGLE = 64
  private static final Int SECOND_LEAD = 43
  private static final Int THIRD_LEAD = 3
  private static final Int FOURTH_LEAD = 1
  private static final Int FIRST_POSITIVE_REACH = SINGLE - 1
  private static final Int FIRST_NEGATIVE_REACH = -SINGLE
  private static final Int SECOND_POSITIVE_REACH = FIRST_POSITIVE_REACH + SECOND_LEAD * TRAIL_COUNT
  private static final Int SECOND_NEGATIVE_REACH = FIRST_NEGATIVE_REACH - SECOND_LEAD * TRAIL_COUNT
  private static final Int THIRD_POSITIVE_REACH = SECOND_POSITIVE_REACH + THIRD_LEAD * TRAIL_COUNT * TRAIL_COUNT
  private static final Int THIRD_NEGATIVE_REACH = SECOND_NEGATIVE_REACH - THIRD_LEAD * TRAIL_COUNT * TRAIL_COUNT
  private static final Int SECOND_POSITIVE_START = MIDDLE + FIRST_POSITIVE_REACH + 1
  private static final Int SECOND_NEGATIVE_START = MIDDLE + FIRST_NEGATIVE_REACH
  private static final Int THIRD_POSITIVE_START = SECOND_POSITIVE_START + SECOND_LEAD
  private static final Int THIRD_NEGATIVE_START = SECOND_NEGATIVE_START - SECOND_LEAD
  private static final Int FOURTH_POSITIVE_START = THIRD_POSITIVE_START + THIRD_LEAD
  private static final Int FOURTH_NEGATIVE_START = THIRD_NEGATIVE_START - THIRD_LEAD
  private static final Int[] TRAILS = [-1, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF, -1, -1, 0x10, 0x11, 0x12, 0x13, -1]

  public BocuDecodableInputStream(InputStream stream) {
    super(stream)
  }

  public Int readUnsignedShort() {
    Int first = read()
    Int second = read()
    if (first >= 0 && second >= 0) {
      return first + (second << 8)
    } else {
      return -1
    }
  }

  public Long readUnsignedInt() {
    Int first = read()
    Int second = read()
    Int third = read()
    Long fourth = read()
    if (first >= 0 && second >= 0 && third >= 0 && fourth >= 0) {
      return first + (second << 8) + (third << 16) + (fourth << 24)
    } else {
      return -1
    }
  }

  // ストリームが終端もしくは 0x0 に到達するまでバイトを読み込み、読み込んだバイトの配列を返します。
  public Byte[] readUntilNull() {
    List<ByteClass> buffer = ArrayList.new()
    for (Int current ; (current = read()) > 0 ;) {
      buffer.add((Byte)current)
    }
    return (Byte[])buffer.toArray()
  }

  // ストリームからデータを読み込み、それを BOCU-1 でエンコードされた文字列だと解釈して、デコードした結果を返します。
  // ストリームが終端もしくは 0x0 に到達するまでバイトを読み込みます。 
  public String decodeStringUntilNull() {
    StringBuilder result = StringBuilder.new()
    Int previous = ASCII_PREVIOUS
    Int current = 0
    while ((current = read()) > 0) {
      Int codePoint = -1
      Int count = 0
      if (current <= 0x20) {
        if (current != 0x20) {
          previous = ASCII_PREVIOUS
        }
        result.appendCodePoint(current)
        continue
      }
      if (current >= SECOND_NEGATIVE_START && current < SECOND_POSITIVE_START) {
        codePoint = previous + current - MIDDLE
        previous = nextPrevious(codePoint)
      } else if (current == RESET) {
        previous = ASCII_PREVIOUS
        continue
      } else {
        if (current >= SECOND_NEGATIVE_START) {
          if (current < THIRD_POSITIVE_START) {
            codePoint = (current - SECOND_POSITIVE_START) * TRAIL_COUNT + FIRST_POSITIVE_REACH + 1
            count = 1
          } else if (current < FOURTH_POSITIVE_START) {
            codePoint = (current - THIRD_POSITIVE_START) * TRAIL_COUNT * TRAIL_COUNT + SECOND_POSITIVE_REACH + 1
            count = 2
          } else {
            codePoint = THIRD_POSITIVE_REACH + 1
            count = 3
          }
        } else {
          if (current >= THIRD_NEGATIVE_START) {
            codePoint = (current - SECOND_NEGATIVE_START) * TRAIL_COUNT + FIRST_NEGATIVE_REACH
            count = 1
          } else if (current > MIN) {
            codePoint = (current - THIRD_NEGATIVE_START) * TRAIL_COUNT * TRAIL_COUNT + SECOND_NEGATIVE_REACH
            count = 2
          } else {
            codePoint = -TRAIL_COUNT * TRAIL_COUNT * TRAIL_COUNT + THIRD_NEGATIVE_REACH
            count = 3
          }
        }
        while (count > 0 && (current = read()) >= 0) {
          Int trail = 0
          if (current <= 0x20) {
            trail = TRAILS[current]
            if (trail < 0) {
              throw MalformedInputException.new(0)
            }
          } else {
            trail = current - TRAIL_BYTE_OFFSET
          }
          if (count == 1) {
            codePoint = previous + codePoint + trail
            if (codePoint >= 0x0 && codePoint <= 0x10FFFF) {
              previous = nextPrevious(codePoint)
              count = 0
              break
            } else {
              throw MalformedInputException.new(0)
            }
          } else if (count == 2) {
            codePoint += trail * TRAIL_COUNT
          } else {
            codePoint += trail * TRAIL_COUNT * TRAIL_COUNT
          }
          count --
        }
        if (count != 0) {
          throw MalformedInputException.new(0)
        }
      }
      if (codePoint >= 0x0 && codePoint <= 0x10FFFF) {
        result.appendCodePoint(codePoint)
      }
    }
    return result.toString()
  }

  // 与えられたバイト列を BOCU-1 でエンコードされた文字列だと解釈して、デコードした結果を返します。
  // バイト列に 0x0 が含まれていた場合は、そこまでのバイト列をデコードします。
  public static String decode(Byte[] buffer) {
    BocuDecodableInputStream stream =  BocuDecodableInputStream.new(ByteArrayInputStream.new(buffer))
    try {
      String string = stream.decodeStringUntilNull()
      return string
    } finally {
      stream.close()
    }
  }

  private Int nextPrevious(Int codePoint) {
    if (codePoint >= 0x3040 && codePoint <= 0x309F) {
      return 0x3070
    } else if (codePoint >= 0x4E00 && codePoint <= 0x9FA5) {
      return 0x4E00 - SECOND_NEGATIVE_REACH
    } else if (codePoint >= 0xAC00 && codePoint <= 0xD7A3) {
      return 0xC1D1
    } else {
      return (codePoint & ~0x7F) + ASCII_PREVIOUS
    }
  }

}