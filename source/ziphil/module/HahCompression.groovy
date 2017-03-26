package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HahCompression {

  private HahCompressionType $type = HahCompressionType.NORMAL
  private Integer $interval = 4
  private String $alphabetOrder = null

  public String compress(String input) {
    List<SortElement> elements = ArrayList.new()
    for (Integer i : 0 ..< input.length()) {
      SortElement element = SortElement.new(input.getAt(i), i)
      element.updateComparisonIndex($alphabetOrder)
      elements.add(element)
    }
    if ($type == HahCompressionType.RANDOM || $type == HahCompressionType.RANDOM_SORT) {
      Collections.shuffle(elements)
    } else if ($type == HahCompressionType.SORT) {
      Collections.sort(elements) { SortElement firstElement, SortElement secondElement ->
        return firstElement.getComparisonIndex() - secondElement.getComparisonIndex()
      }
    }
    List<SortElement> compressedElements = ArrayList.new()
    if (elements.size() > 0) {
      Integer max = (Integer)(elements.size() - 1).intdiv($interval)
      for (Integer i : 0 .. max) {
        Integer start = $interval * i
        Integer end = ($interval * (i + 1) <= elements.size()) ? $interval * (i + 1) - 1 : elements.size() - 1
        compressedElements.add(elements[start])
        if (start < end) {
          compressedElements.add(elements[end])
        }
      }
    }
    if ($type == HahCompressionType.RANDOM_SORT || $type == HahCompressionType.SORT) {
      Collections.sort(compressedElements) { SortElement firstElement, SortElement secondElement ->
        return firstElement.getIndex() - secondElement.getIndex()
      }
    }
    StringBuilder output = StringBuilder.new()
    for (SortElement element : compressedElements) {
      output.append(element.getCharacter())
    }
    return output.toString()
  }

  public void setType(HahCompressionType type) {
    $type = type
  }

  public void setInterval(Integer interval) {
    $interval = interval
  }

  public void setAlphabetOrder(String alphabetOrder) {
    $alphabetOrder = alphabetOrder
  }

}


@InnerClass(HahCompression)
private static class SortElement {

  private String $character
  private Integer $comparisonIndex = -1
  private Integer $index = -1

  public SortElement(String character, Integer index) {
    $character = character
    $index = index
  }

  public void updateComparisonIndex(String order) {
    if (order != null) {
      $comparisonIndex = order.indexOf($character)
    } else {
      $comparisonIndex = $character.codePointAt(0)
    }
  }

  public String getCharacter() {
    return $character
  }

  public Integer getComparisonIndex() {
    return $comparisonIndex
  }

  public Integer getIndex() {
    return $index
  }

}