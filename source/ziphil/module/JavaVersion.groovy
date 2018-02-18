package ziphil.module

import groovy.transform.CompileStatic
import java.util.regex.Matcher
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class JavaVersion implements Comparable<JavaVersion> {

  private Int $major = -1
  private Int $minor = 0
  private Int $update = 0

  private JavaVersion(Int major, Int minor, Int update) {
    $major = major
    $minor = minor
    $update = update
  }

  public static JavaVersion parseString(String string) {
    List<String> splitString = string.split(/\.|_/).toList()
    Int major = -1
    Int minor = 0
    Int update = 0
    if (splitString[0] == "1") {
      if (splitString[1] != null) {
        Matcher majorMatcher = splitString[1] =~ /^(\d+)/
        if (majorMatcher.find()) {
          major = IntegerClass.parseInt(majorMatcher.group(1))
        }
      }
      if (splitString[2] != null) {
        Matcher minorMatcher = splitString[2] =~ /^(\d+)/
        if (minorMatcher.find()) {
          minor = IntegerClass.parseInt(minorMatcher.group(1))
        }
      }
      if (splitString[3] != null) {
        Matcher updateMatcher = splitString[3] =~ /^(\d+)/
        if (updateMatcher.find()) {
          update = IntegerClass.parseInt(updateMatcher.group(1))
        }
      }
    } else {
      if (splitString[0] != null) {
        Matcher majorMatcher = splitString[0] =~ /^(\d+)/
        if (majorMatcher.find()) {
          major = IntegerClass.parseInt(majorMatcher.group(1))
        }
      }
      if (splitString[1] != null) {
        Matcher minorMatcher = splitString[1] =~ /^(\d+)/
        if (minorMatcher.find()) {
          minor = IntegerClass.parseInt(minorMatcher.group(1))
        }
      }
      if (splitString[2] != null) {
        Matcher updateMatcher = splitString[2] =~ /^(\d+)/
        if (updateMatcher.find()) {
          update = IntegerClass.parseInt(updateMatcher.group(1))
        }
      }
    }
    return JavaVersion.new(major, minor, update)
  }

  public static JavaVersion current() {
    String string = System.getProperty("java.version")
    return parseString(string)
  }

  public Int compareTo(JavaVersion other) {
    if ($major > other.getMajor()) {
      return 1
    } else if ($major < other.getMajor()) {
      return -1
    } else {
      if ($minor > other.getMinor()) {
        return 1
      } else if ($minor < other.getMinor()) {
        return -1
      } else {
        if ($update > other.getUpdate()) {
          return 1
        } else if ($update < other.getUpdate()) {
          return -1
        } else {
          return 0
        }
      }
    }
  }

  public String toString() {
    if ($major >= 9) {
      return "${$major}.${$minor}.${$update}"
    } else {
      if ($update > 0) {
        return "1.${$major}.${$minor}_${$update} (${$major}u${$update})"
      } else {
        return "1.${$major}.${$minor}"
      }
    }
  }

  private Int getMajor() {
    return $major
  }

  private Int getMinor() {
    return $minor
  }

  private Int getUpdate() {
    return $update
  }

}