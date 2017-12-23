package ziphil.dictionary.slime.exporter

import groovy.transform.CompileStatic
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.Fop
import org.apache.fop.apps.MimeConstants
import ziphil.dictionary.ExportConfig
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class SlimePdfDictionaryExporter extends SlimeTemporaryXmlDictionaryExporter {

  private static final String XSL_PATH = "resource/xsl/slime_pdf.xsl"
  private static final String CONFIG_PATH = "resource/xsl/config.xml"
  private static final Boolean USES_SAXON_TRANSFORMER = false

  private ExportConfig $config

  public SlimePdfDictionaryExporter(ExportConfig config) {
    super()
    $config = config
  }

  protected BooleanClass saveMain() {
    println("start")
    transformDebug()
    updateProgress(1, 2)
    println("finish debug")
    transform()
    updateProgress(2, 2)    
    println("finish")
  }

  protected BooleanClass transform() {
    File file = File.new($path)
    File temporaryFile = File.new($temporaryPath)
    BufferedOutputStream stream = file.newOutputStream()
    BufferedInputStream temporaryStream = temporaryFile.newInputStream()
    try {
      FopFactory fopFactory = FopFactory.newInstance(File.new(".").toURI(), getClass().getClassLoader().getResourceAsStream(CONFIG_PATH))
      Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, stream)
      Result result = SAXResult.new(fop.getDefaultHandler())
      Source source = StreamSource.new(temporaryStream)
      Transformer transformer = createTransformer()
      transformer.transform(source, result)
    } finally {
      stream.close()
      temporaryStream.close()
    }
  }

  protected BooleanClass transformDebug() {
    File file = File.new($path.replaceAll(/\.\w+$/, "_debug.xml"))
    File temporaryFile = File.new($temporaryPath)
    BufferedOutputStream stream = file.newOutputStream()
    BufferedInputStream temporaryStream = temporaryFile.newInputStream()
    try {
      Result result = StreamResult.new(stream)
      Source source = StreamSource.new(temporaryStream)
      Transformer transformer = createTransformer()
      transformer.transform(source, result)
    } finally {
      stream.close()
      temporaryStream.close()
    }
  }

  private Transformer createTransformer() {
    Source xslSource = StreamSource.new(getClass().getClassLoader().getResourceAsStream(XSL_PATH))
    TransformerFactory factory = (USES_SAXON_TRANSFORMER) ? TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null) : TransformerFactory.newInstance()
    Transformer transformer = factory.newTransformer(xslSource)
    transformer.setParameter("punctuation", ", ")
    return transformer
  }

}