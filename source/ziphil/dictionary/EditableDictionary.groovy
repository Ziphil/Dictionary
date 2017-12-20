package ziphil.dictionary

import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import javafx.concurrent.Task


@CompileStatic 
public interface EditableDictionary<W extends Word, V extends Word> {

  // oldWord と同値な単語データが newWord に変更されたことをこの辞書に通知します。
  // さらに、変更された内容に従って、内部データの更新を行います。
  // このメソッドによって、辞書内に含まれる oldWord が newWord に書き換わるわけではありません。
  // 単語データの内容を変更したい場合は、変更前のその単語データのコピーを oldWord に渡し、変更前と同一の変更後の単語データを newWord に渡してください。
  // 単語データのコピーには copyWord() を用いてください。
  public void modifyWord(W oldWord, V newWord)

  // word に渡された単語データをこの辞書に追加し、内部データの更新を行います。
  // 内部データの更新が毎回行われてパフォーマンスが低下するのを防ぐため、複数の単語データを追加したい場合は、addWords() を利用してください。
  public void addWord(V word)

  // words に渡されたリストに含まれる全ての単語データをこの辞書に追加し、最後に内部データの更新を行います。
  public void addWords(List<? extends V> words)

  // word に渡された単語データをこの辞書から削除し、内部データの更新を行います。
  // この辞書が word と同一の単語データを含んでいない場合は、何も処理を行いません。
  // 内部データの更新が毎回行われてパフォーマンスが低下するのを防ぐため、複数の単語データを削除したい場合は、removeWords() を利用してください。
  public void removeWord(V word)

  // words に渡されたリストに含まれる全ての単語データをこの辞書から削除し、最後に内部データの更新を行います。
  public void removeWords(List<? extends V> words)

  // 辞書内に含まれる mergedWord に removedWord が統合されたことをこの辞書に通知し、removedWord をこの辞書から削除します。
  // さらに、変更された内容に従って、内部データの更新を行います。
  // このメソッドは、単語データの統合が行われたことを通知するのみで、実際に統合処理を行うわけではありません。
  public void mergeWord(V mergedWord, V removedWord)

  public V createWord(String defaultName)

  public V copyWord(W oldWord)

  public V inheritWord(W oldWord)

  public V determineWord(String name, PseudoWord psuedoWord)

  public EditorControllerFactory getEditorControllerFactory()

}