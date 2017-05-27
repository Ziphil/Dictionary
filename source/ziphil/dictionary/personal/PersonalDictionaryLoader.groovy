package ziphil.dictionary.personal

import com.orangesignal.csv.CsvConfig
import com.orangesignal.csv.CsvReader
import com.orangesignal.csv.io.CsvColumnNameMapReader
import groovy.transform.CompileStatic
import javafx.collections.ObservableList
import ziphil.dictionary.DictionaryLoader
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PersonalDictionaryLoader extends DictionaryLoader<PersonalDictionary, PersonalWord> {

  public PersonalDictionaryLoader(PersonalDictionary dictionary, String path) {
    super(dictionary, path)
  }

  protected Boolean load() {
    File file = File.new($path)
    BufferedReader reader = file.newReader()
    CsvConfig config = createConfig()
    CsvColumnNameMapReader csvReader = CsvColumnNameMapReader.new(CsvReader.new(reader, config))
    try {
      for (Map<String, String> line ; (line = csvReader.read()) != null ;) {
        if (isCancelled()) {
          return false
        }
        PersonalWord word = PersonalWord.new()
        fillWord(word, line)
        $words.add(word)
      }
    } finally {
      csvReader.close()
    }
    return true
  }

  private void fillWord(PersonalWord word, Map<String, String> line) {
    String name = line["word"]
    String pronunciation = line["pron"]
    String translation = line["trans"]
    String usage = line["exp"]
    String level = line["level"]
    String memory = line["memory"]
    String modification = line["modify"]
    if (name != null) {
      word.setName(name)
    }
    if (pronunciation != null) {
      word.setPronunciation(pronunciation)
    }
    if (translation != null) {
      word.setTranslation(translation)
    }
    if (usage != null) {
      word.setUsage(usage)
    }
    if (level != null) {
      word.setLevel(level.toInteger())
    }
    if (memory != null) {
      word.setMemory(memory.toInteger())
    }
    if (modification != null) {
      word.setModification(modification.toInteger())
    }
  }

  private CsvConfig createConfig() {
    CsvConfig config = CsvConfig.new()
    config.setQuoteDisabled(false)
    config.setEscapeDisabled(false)
    config.setIgnoreEmptyLines(true)
    config.setEscape((Character)'"')
    return config
  }

}