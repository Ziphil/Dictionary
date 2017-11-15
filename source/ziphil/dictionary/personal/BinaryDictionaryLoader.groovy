package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryLoader
import ziphil.module.BocuDecoder
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionaryLoader extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  public BinaryDictionaryLoader(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected BooleanClass load() {
    File file = File.new($path)
    BufferedInputStream stream = file.newInputStream()
    BocuDecoder decoder = BocuDecoder.new(stream)
    try {
      Byte[] header = Byte[].new(256)
      stream.read(header)
      Int version = header[0x8C] + (header[0x8D] << 8)
      Int indexBlockSize = header[0x94] + (header[0x95] << 8)
      Int wordSize = header[0xA0] + (header[0xA1] << 8)
      Int type = header[0xA5]
      Int indexBlockBitSize = header[0xB6]
      Int extendedHeaderSize = header[0xB8] + (header[0xB9] << 8) + (header[0xBA] << 16) + (header[0xBB] << 24)
      Int indexSize = header[0xC0] + (header[0xC1] << 8) + (header[0xC2] << 16) + (header[0xC3] << 24)
      Int dataBlockSize = header[0xC4] + (header[0xC5] << 8) + (header[0xC6] << 16) + (header[0xC7] << 24)
      if ((version >> 8) != 6) {
        throw IllegalArgumentException.new("Unsupported version")
      }
      if ((type & 0x40) != 0) {
        throw IllegalArgumentException.new("Encrypted")
      }
      Int skippedSize = extendedHeaderSize + indexBlockSize * 1024 + 768
      for (Int i = 0 ; i < skippedSize ; i ++) {
        stream.read()
      }
      readDataBlocks(stream)
    } finally {
      stream.close()
    }
    return true
  }

  private void readDataBlocks(InputStream stream) {
    while (true) {
      Int next = stream.read()
      Int rawLength = next + (stream.read() << 8)
      Int length = (rawLength & 0x7FFF) * 1024
      Int fieldLength = ((rawLength & 0x8000) != 0) ? 4 : 2
      if (next >= 0) { 
        if (length > 0) {
          Byte[] buffer = Byte[].new(length - 2)
          stream.read(buffer)
          ByteArrayInputStream nextStream = ByteArrayInputStream.new(buffer)
          addWords(nextStream, fieldLength)
        } else {
          stream.skip(1022)
        }
      } else {
        break
      }
    }
  }

  private void addWords(InputStream stream, Int fieldLength) {
    while (true) {
      Int length = readLength(stream, fieldLength)
      if (length > 0) {
        PersonalWord word = PersonalWord.new()
        Int omittedKeywordLength = stream.read()
        Int flag = stream.read()
        Boolean hasExtension = (flag & 0x10) != 0
        Int level = flag & 0x0F
        Int memory = ((flag & 0x20) != 0) ? 1 : 0
        Int modification = ((flag & 0x40) != 0) ? 1 : 0
        Byte[] buffer = Byte[].new(length)
        stream.read(buffer)
        ByteArrayInputStream nextStream = ByteArrayInputStream.new(buffer)
        BocuDecoder decoder = BocuDecoder.new(nextStream)
        String rawName = decoder.decodeUntilNull()
        Int nameTabIndex = rawName.indexOf("\t")
        String name = (nameTabIndex < 0) ? rawName : rawName.substring(nameTabIndex + 1)
        String translation = decoder.decodeUntilNull()
        word.setName(name)
        word.setTranslation(translation)
        word.setLevel(level)
        word.setMemory(memory)
        word.setModification(modification)
        if (hasExtension) {
          fillExtensions(nextStream, decoder, fieldLength, word)
        }
        $words.add(word)
      } else {
        break
      }
    }
  }

  private void fillExtensions(InputStream stream, BocuDecoder decoder, Int fieldLength, PersonalWord word) {
    while (true) {
      Int flag = stream.read()
      Int type = flag & 0xF
      if ((flag & 0x80) == 0) {
        if ((flag & 0x10) == 0) {
          String text = decoder.decodeUntilNull()
          if (type == 1) {
            word.setUsage(text)
          } else if (type == 2) {
            word.setPronunciation(text)
          }
        } else {
          Int length = readLength(stream, fieldLength)
          stream.skip(length)
        }
      } else {
        break
      }
    }
  }

  private Int readLength(InputStream stream, Int fieldLength) {
    Int length = stream.read()
    if (length >= 0) {
      length += (stream.read() << 8)
      if (fieldLength == 4) {
        length += (stream.read() << 16) + (stream.read() << 24)
      }
      return length
    } else {
      return -1
    }
  }

}