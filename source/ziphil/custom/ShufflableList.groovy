package ziphil.custom

import com.sun.javafx.collections.NonIterableChange.SimplePermutationChange
import com.sun.javafx.collections.SourceAdapterChange
import groovy.transform.CompileStatic
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import ziphilib.transform.ConvertPrimitive


@CompileStatic @Newify
public class ShufflableList<E> extends TransformationList<E, E> {

  private List<Integer> $indices
  private Integer $size = 0
  private Boolean $isShuffled = false

  public ShufflableList(ObservableList<? extends E> source) {
    super(source)
    $indices = ArrayList.new(0 ..< source.size())
    $size = source.size()
  }

  public void shuffle() {
    List<Integer> oldIndices = ArrayList.new($indices)
    Collections.shuffle($indices)
    updatePermutation(oldIndices)
    $isShuffled = true
  }

  public void unshuffle() {
    if ($isShuffled) {
      List<Integer> oldIndices = ArrayList.new($indices)
      $indices = ArrayList.new(0 ..< $size)
      updatePermutation(oldIndices)
      $isShuffled = false
    }
  }

  private List<Integer> updatePermutation(List<Integer> oldIndices) {
    List<Integer> permutation = ArrayList.new($size)
    (0 ..< $size).each() { Integer i ->
      permutation[oldIndices[i]] = $indices[i]
    }
    beginChange()
    nextPermutation(0, $size, (Integer[])permutation.toArray())
    endChange()
  }

  protected void sourceChanged(Change<? extends E> change) {
    beginChange()
    while (change.next()) {
      Integer from = change.getFrom()
      Integer to = change.getTo()
      if (change.wasPermutated()) {
        List<Integer> permutation = ArrayList.new($size)
        (0 ..< $size).each() { Integer i ->
          permutation[i] = change.getPermutation($indices[i])
        }
        nextPermutation(0, $size, (Integer[])permutation.toArray())
      } else if (change.wasUpdated()) {
        (from ..< to).each() { Integer i ->
          nextUpdate($indices[i])
        }
      } else {
        Integer newSize = change.getList().size()
        if (newSize > $size) {
          $indices.addAll($size ..< newSize)
        } else if (newSize < $size) {
          $indices.removeIf{index -> (Integer)index >= newSize}
        }
        nextReplace(from, to, change.getRemoved())
        $size = newSize
      }
    }
    endChange()
  }

  @ConvertPrimitive
  public Integer size() {
    return $size
  }

  @ConvertPrimitive
  public E get(Integer index) {
    if (index >= $size) {
      throw IndexOutOfBoundsException.new()
    }
    return getSource()[$indices[index]]
  }

  @ConvertPrimitive
  public Integer getSourceIndex(Integer index) {
    return $indices[index]
  }

}