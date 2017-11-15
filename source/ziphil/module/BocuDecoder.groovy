package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BocuDecoder {

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

  private InputStream $stream

  public BocuDecoder(InputStream stream) {
    $stream = stream
  }

  // ストリームから BOCU-1 エンコードされたデータを読み込み、デコードした結果の文字列を返します。
  // ストリームが終端もしくは 0x0 に到達するまで文字列を読み込みます。 
  public String decodeUntilNull() {
    StringBuilder result = StringBuilder.new()
    Int previous = ASCII_PREVIOUS
    Int next = 0
    while ((next = $stream.read()) > 0) {
      Int codePoint = -1
      Int count = 0
      if (next <= 0x20) {
        if (next != 0x20) {
          previous = ASCII_PREVIOUS
        }
        result.appendCodePoint(next)
        continue
      }
      if (next >= SECOND_NEGATIVE_START && next < SECOND_POSITIVE_START) {
        codePoint = previous + next - MIDDLE
        previous = nextPrevious(codePoint)
      } else if (next == RESET) {
        previous = ASCII_PREVIOUS
        continue
      } else {
        if (next >= SECOND_NEGATIVE_START) {
          if (next < THIRD_POSITIVE_START) {
            codePoint = (next - SECOND_POSITIVE_START) * TRAIL_COUNT + FIRST_POSITIVE_REACH + 1
            count = 1
          } else if (next < FOURTH_POSITIVE_START) {
            codePoint = (next - THIRD_POSITIVE_START) * TRAIL_COUNT * TRAIL_COUNT + SECOND_POSITIVE_REACH + 1
            count = 2
          } else {
            codePoint = THIRD_POSITIVE_REACH + 1
            count = 3
          }
        } else {
          if (next >= THIRD_NEGATIVE_START) {
            codePoint = (next - SECOND_NEGATIVE_START) * TRAIL_COUNT + FIRST_NEGATIVE_REACH
            count = 1
          } else if (next > MIN) {
            codePoint = (next - THIRD_NEGATIVE_START) * TRAIL_COUNT * TRAIL_COUNT + SECOND_NEGATIVE_REACH
            count = 2
          } else {
            codePoint = -TRAIL_COUNT * TRAIL_COUNT * TRAIL_COUNT + THIRD_NEGATIVE_REACH
            count = 3
          }
        }
        while (count > 0 && (next = $stream.read()) >= 0) {
          Int trail = 0
          if (next <= 0x20) {
            trail = TRAILS[next]
            if (trail < 0) {
              throw IllegalArgumentException.new("Illegal trail byte values")
            }
          } else {
            trail = next - TRAIL_BYTE_OFFSET
          }
          if (count == 1) {
            codePoint = previous + codePoint + trail
            if (codePoint >= 0x0 && codePoint <= 0x10FFFF) {
              previous = nextPrevious(codePoint)
              count = 0
              break
            } else {
              throw IllegalArgumentException.new("Illegal code point result: 0x${String.format("%x", codePoint)}")
            }
          } else if (count == 2) {
            codePoint += trail * TRAIL_COUNT
          } else {
            codePoint += trail * TRAIL_COUNT * TRAIL_COUNT
          }
          count --
        }
        if (count != 0) {
          throw IllegalArgumentException.new("Deficient trail bytes")
        }
      }
      if (codePoint >= 0x0 && codePoint <= 0x10FFFF) {
        result.appendCodePoint(codePoint)
      }
    }
    return result.toString()
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