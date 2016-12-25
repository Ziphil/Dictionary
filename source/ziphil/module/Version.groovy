package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.ConvertPrimitiveArgs
import ziphilib.transform.Ziphilify


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

  public Version(List<Integer> versionList) {
    $major = versionList[0] ?: -1
    $minor = versionList[1] ?: 0
    $patch = versionList[2] ?: 0
    $date = versionList[3]
  }

  public Version(Integer major, Integer minor, Integer patch) {
    this(major, minor, patch, null)
  }

  @ConvertPrimitiveArgs
  public Integer compareTo(Version other) {
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