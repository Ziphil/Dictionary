package ziphil.dictionary.personal

import groovy.transform.CompileStatic
import ziphil.dictionary.DictionaryLoader
import ziphil.module.BocuDecodableInputStream
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class BinaryDictionaryLoader extends DictionaryLoader<BinaryDictionary, PersonalWord> {

  public BinaryDictionaryLoader(BinaryDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected BooleanClass load() {
    File file = File.new($path)
    BocuDecodableInputStream stream = BocuDecodableInputStream.new(file.newInputStream())
    try {
      Byte[] header = Byte[].new(256)
      stream.read(header)
      Int version = header[0x8C] + (header[0x8D] << 8)
      Int type = header[0xA5]
      Int indexBlockSize = header[0x94] + (header[0x95] << 8)
      Int wordSize = header[0xA0] + (header[0xA1] << 8)
      Int extendedHeaderSize = header[0xB8] + (header[0xB9] << 8) + (header[0xBA] << 16) + (header[0xBB] << 24)
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

  private void readDataBlocks(BocuDecodableInputStream stream) {
    while (true) {
      Int rawLength = stream.readUnsignedShort()
      Int length = (rawLength & 0x7FFF) * 1024
      Int fieldLength = ((rawLength & 0x8000) != 0) ? 4 : 2
      if (rawLength >= 0) {
        if (length > 0) {
          Byte[] buffer = Byte[].new(length - 2)
          stream.read(buffer)
          BocuDecodableInputStream nextStream = BocuDecodableInputStream.new(ByteArrayInputStream.new(buffer))
          addWords(nextStream, fieldLength)
        } else {
          for (Int i = 0 ; i < 1022 ; i ++) {
            stream.read()
          }
        }
      } else {
        break
      }
    }
  }

  private void addWords(BocuDecodableInputStream stream, Int fieldLength) {
    while (true) {
      Long length = (fieldLength == 2) ? stream.readUnsignedShort() : stream.readUnsignedInt()
      if (length > 0) {
        PersonalWord word = PersonalWord.new()
        Int omittedKeywordLength = stream.read()
        Int flag = stream.read()
        Byte[] buffer = Byte[].new(length)
        stream.read(buffer)
        BocuDecodableInputStream nextStream = BocuDecodableInputStream.new(ByteArrayInputStream.new(buffer))
        Int level = flag & 0x0F
        Int memory = ((flag & 0x20) != 0) ? 1 : 0
        Int modification = ((flag & 0x40) != 0) ? 1 : 0
        String rawName = nextStream.decodeStringUntilNull()
        Int nameTabIndex = rawName.indexOf("\t")
        String name = (nameTabIndex >= 0) ? rawName.substring(nameTabIndex + 1) : rawName
        String translation = nextStream.decodeStringUntilNull()
        word.setName(name)
        word.setTranslation(translation)
        word.setLevel(level)
        word.setMemory(memory)
        word.setModification(modification)
        if ((flag & 0x10) != 0) {
          fillExtensions(nextStream, fieldLength, word)
        }
        $words.add(word)
      } else {
        break
      }
    }
  }

  private void fillExtensions(BocuDecodableInputStream stream, Int fieldLength, PersonalWord word) {
    while (true) {
      Int flag = stream.read()
      Int type = flag & 0xF
      if ((flag & 0x80) == 0) {
        if ((flag & 0x10) == 0) {
          String text = stream.decodeStringUntilNull()
          if (type == 1) {
            word.setUsage(text)
          } else if (type == 2) {
            word.setPronunciation(text)
          }
        } else {
          Long length = (fieldLength == 2) ? stream.readUnsignedShort() : stream.readUnsignedInt()
          for (Long i = 0 ; i < length ; i ++) {
            stream.read()
          }
        }
      } else {
        break
      }
    }
  }

}