package ziphil.custom

import com.sun.javafx.collections.NonIterableChange.SimplePermutationChange
import com.sun.javafx.collections.SourceAdapterChange
import groovy.transform.CompileStatic
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import ziphilib.transform.Ziphilify
import ziphilib.type.PrimInt


@CompileStatic @Ziphilify
public class ShufflableList<E> extends TransformationList<E, E> {

  private List<Integer> $indices
  private Integer $size = 0
  private Boolean $shuffled = false

  public ShufflableList(ObservableList<? extends E> source) {
    super(source)
    $indices = ArrayList.new(0 ..< source.size())
    $size = source.size()
  }

  public void shuffle() {
    List<Integer> oldIndices = ArrayList.new($indices)
    Collections.shuffle($indices)
    updatePermutation(oldIndices)
    $shuffled = true
  }

  public void unshuffle() {
    if ($shuffled) {
      List<Integer> oldIndices = ArrayList.new($indices)
      $indices = ArrayList.new(0 ..< $size)
      updatePermutation(oldIndices)
      $shuffled = false
    }
  }

  private void updatePermutation(List<Integer> oldIndices) {
    Integer[] permutation = Integer[].new($size)
    for (Integer i : 0 ..< $size) {
      permutation[oldIndices[i]] = $indices[i]
    }
    beginChange()
    nextPermutation(0, $size, permutation)
    endChange()
  }

  protected void sourceChanged(Change<? extends E> change) {
    beginChange()
    while (change.next()) {
      Integer from = change.getFrom()
      Integer to = change.getTo()
      if (change.wasPermutated()) {
        Integer[] permutation = Integer[].new($size)
        for (Integer i : 0 ..< $size) {
          permutation[i] = change.getPermutation($indices[i])
        }
        nextPermutation(0, $size, permutation)
      } else if (change.wasUpdated()) {
        for (Integer i : from ..< to) {
          nextUpdate($indices[i])
        }
      } else {
        Integer newSize = change.getList().size()
        if (newSize > $size) {
          $indices.addAll($size ..< newSize)
        } else if (newSize < $size) {
          $indices.removeIf{it >= newSize}
        }
        nextReplace(from, to, change.getRemoved())
        $size = newSize
      }
    }
    endChange()
  }

  public PrimInt size() {
    return $size
  }

  public E get(PrimInt index) {
    if (index >= $size) {
      throw IndexOutOfBoundsException.new()
    }
    return getSource()[$indices[index]]
  }

  public PrimInt getSourceIndex(PrimInt index) {
    return $indices[index]
  }

}