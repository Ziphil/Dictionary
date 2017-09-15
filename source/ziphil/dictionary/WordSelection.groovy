package ziphil.dictionary

import groovy.transform.CompileStatic
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class WordSelection implements Transferable, ClipboardOwner {

  public static final DataFlavor WORD_FLAVOR = DataFlavor.new(List, "zpdic-word-list")

  private List<Word> $words
  private ClipboardOwner $owner

  public WordSelection(List<Word> words, ClipboardOwner owner) {
    $words = words
    $owner = owner
  }

  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    $owner.lostOwnership(clipboard, contents)
    $words = null
    $owner = null
  }

  public List<Word> getTransferData(DataFlavor flavor) {
    if (flavor == WORD_FLAVOR) {
      return $words
    } else {
      throw UnsupportedFlavorException.new(flavor)
    }
  }

  public DataFlavor[] getTransferDataFlavors() {
    return (DataFlavor[])[WORD_FLAVOR].toArray()
  }

  public Boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor == WORD_FLAVOR
  }

}