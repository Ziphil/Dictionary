package ziphil.module

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimInt


@CompileStatic @Ziphilify
public class Version implements Comparable<Version> {

  private Integer $major = -1
  private Integer $minor = 0
  private Integer $patch = 0
  private Integer $date = null

  public Version(Integer major, Integer minor, Integer patch, Integer date) {
    $major = major
    $minor = minor
    $patch = patch
    $date = date
  }

  public Version(Integer major, Integer minor, Integer patch) {
    this(major, minor, patch, null)
  }

  @JsonCreator
  public Version(List<Integer> versionList) {
    $major = versionList[0] ?: -1
    $minor = versionList[1] ?: 0
    $patch = versionList[2] ?: 0
    $date = versionList[3]
  }

  public PrimInt compareTo(Version other) {
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
    if (date != null) {
      return "${$major}.${$minor}.${$patch}-${$date}"
    } else {
      return "${$major}.${$minor}.${$patch}"
    }
  }

  @JsonValue
  public List<Integer> toList() {
    return [$major, $minor, $patch]
  }

  public Integer getMajor() {
    return $major
  }

  public Integer getMinor() {
    return $minor
  }

  public Integer getPatch() {
    return $patch
  }

  public Integer getDate() {
    return $date
  }

}