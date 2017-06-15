package ziphil.module

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Version implements Comparable<Version> {

  private Int $major = -1
  private Int $minor = 0
  private Int $patch = 0
  private Int $date = -1

  public Version(Int major, Int minor, Int patch, Int date) {
    $major = major
    $minor = minor
    $patch = patch
    $date = date
  }

  public Version(Int major, Int minor, Int patch) {
    this(major, minor, patch, -1)
  }

  @JsonCreator
  public Version(List<IntegerClass> versionList) {
    $major = (versionList[0] != null) ? versionList[0] : -1
    $minor = (versionList[1] != null) ? versionList[1] : 0
    $patch = (versionList[2] != null) ? versionList[2] : 0
    $date = (versionList[3] != null) ?versionList[3] : -1
  }

  public Int compareTo(Version other) {
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
        if ($patch > other.getPatch()) {
          return 1
        } else if ($patch < other.getPatch()) {
          return -1
        } else {
          return 0
        }
      }
    }
  }

  public String toString() {
    if ($date >= 0) {
      return "${$major}.${$minor}.${$patch}-${$date}"
    } else {
      return "${$major}.${$minor}.${$patch}"
    }
  }

  @JsonValue
  public List<IntegerClass> toList() {
    return [$major, $minor, $patch]
  }

  public Int getMajor() {
    return $major
  }

  public Int getMinor() {
    return $minor
  }

  public Int getPatch() {
    return $patch
  }

  public Int getDate() {
    return $date
  }

}