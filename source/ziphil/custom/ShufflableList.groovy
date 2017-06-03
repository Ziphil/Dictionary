package ziphil.custom

import com.sun.javafx.collections.NonIterableChange.SimplePermutationChange
import com.sun.javafx.collections.SourceAdapterChange
import groovy.transform.CompileStatic
import javafx.collections.ListChangeListener.Change
import javafx.collections.ObservableList
import javafx.collections.transformation.TransformationList
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShufflableList<E> extends TransformationList<E, E> {

  private Int[] $indices
  private Int $size = 0
  private Boolean $shuffled = false

  public ShufflableList(ObservableList<? extends E> source) {
    super(source)
    $indices = (Int[])(0 ..< source.size()).toArray()
    $size = source.size()
  }

  public void shuffle() {
    Int[] oldIndices = $indices
    List<IntegerClass> convertedIndices = $indices.toList()
    Collections.shuffle(convertedIndices)
    $indices = (Int[])convertedIndices.toArray()
    updatePermutation(oldIndices)
    $shuffled = true
  }

  public void unshuffle() {
    if ($shuffled) {
      Int[] oldIndices = $indices
      $indices = (Int[])(0 ..< $size).toArray()
      updatePermutation(oldIndices)
      $shuffled = false
    }
  }

  private void updatePermutation(Int[] oldIndices) {
    Int[] permutation = Int[].new($size)
    for (Int i = 0 ; i < $size ; i ++) {
      permutation[oldIndices[i]] = $indices[i]
    }
    beginChange()
    nextPermutation(0, $size, permutation)
    endChange()
  }

  protected void sourceChanged(Change<? extends E> change) {
    beginChange()
    while (change.next()) {
      Int from = change.getFrom()
      Int to = change.getTo()
      if (change.wasPermutated()) {
        Int[] permutation = Int[].new($size)
        for (Int i = 0 ; i < $size ; i ++) {
          permutation[i] = change.getPermutation($indices[i])
        }
        nextPermutation(0, $size, permutation)
      } else if (change.wasUpdated()) {
        for (Int i = from ; i < to ; i ++) {
          nextUpdate($indices[i])
        }
      } else {
        Int newSize = change.getList().size()
        if (newSize > $size) {
          $indices = Arrays.copyOf($indices, newSize)
          for (Int i = $size ; i < newSize ; i ++) {
            $indices[i] = i
          }
        } else if (newSize < $size) {
          Int[] oldIndices = $indices
          Int pointer = 0
          $indices = Int[].new(newSize)
          for (Int oldIndex : oldIndices) {
            if (oldIndex < newSize) {
              $indices[pointer ++] = oldIndex
            }
          }
        }
        nextReplace(from, to, change.getRemoved())
        $size = newSize
      }
    }
    endChange()
  }

  public Int size() {
    return $size
  }

  public E get(Int index) {
    if (index >= $size) {
      throw IndexOutOfBoundsException.new()
    }
    return getSource()[$indices[index]]
  }

  public Int getSourceIndex(Int index) {
    return $indices[index]
  }

}