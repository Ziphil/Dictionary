package ziphil.module

import com.orangesignal.csv.CsvConfig
import com.orangesignal.csv.CsvWriter
import com.orangesignal.csv.QuotePolicy
import groovy.transform.CompileStatic
import javafx.concurrent.Task
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class CharacterFrequencySaver<D extends Dictionary> extends Task<BooleanClass> {

  private CharacterFrequencyAnalyzer $analyzer
  private String $path

  public CharacterFrequencySaver(CharacterFrequencyAnalyzer analyzer, String path) {
    $analyzer = analyzer
    $path = path
  }

  private BooleanClass save() {
    File file = File.new($path)
    BufferedWriter rawWriter = file.newWriter()
    CsvConfig config = createConfig()
    CsvWriter writer = CsvWriter.new(rawWriter, config)
    try {
      for (CharacterStatus status : $analyzer.characterStatuses()) {
        List<String> values = ArrayList.new()
        values.add(status.getCharacter())
        values.add(status.getFrequency().toString())
        values.add(status.getFrequencyPercentage().toString())
        values.add(status.getUsingWordSize().toString())
        values.add(status.getUsingWordSizePercentage().toString())
        writer.writeValues(values)
      }
    } finally {
      writer.close()
    }
    return true
  }

  protected BooleanClass call() {
    if ($path != null) {
      BooleanClass result = save()
      return result
    } else {
      return false
    }
  }

  private CsvConfig createConfig() {
    CsvConfig config = CsvConfig.new()
    config.setQuoteDisabled(false)
    config.setQuotePolicy(QuotePolicy.MINIMAL)
    config.setEscapeDisabled(false)
    config.setEscape((Char)'"')
    return config
  }

}