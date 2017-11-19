package ziphil.plugin

import groovy.transform.CompileStatic
import javafx.stage.StageStyle
import javafx.stage.Modality
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import ziphil.controller.Controller
import ziphil.controller.ShaleiaUploaderController
import ziphil.custom.Dialog
import ziphil.custom.UtilityStage
import ziphil.dictionary.Dictionary
import ziphil.dictionary.shaleia.ShaleiaDictionary
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ShaleiaUploadPlugin implements Plugin {

  private static final String NAME = "辞典アップロード"
  private static final String DEFAULT_URL_TEXT = "http://ziphil.com/conlang/database/2.cgi"
  private static final String BOUNDARY = createBoundary()

  public void call(Dictionary dictionary) {
    if (isSupported(dictionary)) {
      UtilityStage<ShaleiaUploaderController.Result> nextStage = UtilityStage.new(StageStyle.UTILITY)
      ShaleiaUploaderController controller = ShaleiaUploaderController.new(nextStage)
      nextStage.initModality(Modality.APPLICATION_MODAL)
      nextStage.initOwner(null)
      controller.prepare(DEFAULT_URL_TEXT)
      nextStage.showAndWait()
      if (nextStage.isCommitted()) {
        upload(dictionary, nextStage.getResult())
      }
    }
  }

  private void upload(Dictionary dictionary, ShaleiaUploaderController.Result result) {
    String urlText = result.getUrlText()
    String password = result.getPassword()
    try {
      URL url = URL.new(urlText)
      URLConnection connection = url.openConnection()
      connection.setDoOutput(true)
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=${BOUNDARY}")
      DataOutputStream outputStream = DataOutputStream.new(connection.getOutputStream())
      try {
        outputStream.writeBytes("--${BOUNDARY}\r\n")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"mode\"\r\n")
        outputStream.writeBytes("\r\n")
        outputStream.writeBytes("upload\r\n")
        outputStream.writeBytes("--${BOUNDARY}\r\n")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"password\"\r\n")
        outputStream.writeBytes("\r\n")
        outputStream.writeBytes("${password}\r\n")
        File file = File.new(dictionary.getPath())
        BufferedInputStream inputStream = file.newInputStream()
        try {
          outputStream.writeBytes("--${BOUNDARY}\r\n")
          outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"dictionary.xdc\"\r\n")
          outputStream.writeBytes("Content-Type: text/plain\r\n")
          outputStream.writeBytes("\r\n")
          for (Int current ; (current = inputStream.read()) >= 0 ;){
            outputStream.write(current)
          }
          outputStream.writeBytes("\r\n")
        } finally {
          inputStream.close()
        }
        outputStream.writeBytes("--${BOUNDARY}--")
        outputStream.flush()
      } finally {
        outputStream.close()
      }
      InputStream inputStream = connection.getInputStream()
      try {
        BufferedReader reader = BufferedReader.new(InputStreamReader.new(inputStream))
        for (String line ; (line = reader.readLine()) != null ;) {
        }
      } finally {
        inputStream.close()
      }
    } catch (Exception exception) {
      Dialog dialog = Dialog.new(StageStyle.UTILITY) 
      dialog.setTitle(Controller.DIALOG_RESOURCES.getString("title.failUploadDictionary"))
      dialog.setContentText(Controller.DIALOG_RESOURCES.getString("contentText.failUploadDictionary"))
      dialog.setAllowsCancel(false)
      dialog.showAndWait()
    }
  }

  private static String createBoundary() {
    String characters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    Random random = Random.new()
    StringBuilder boundary = StringBuilder.new()
    for (Int i = 0 ; i < 30 ; i ++) {
      Int index = random.nextInt(characters.length())
      boundary.append(characters.charAt(index))
    }
    return boundary.toString()
  }

  public Boolean isSupported(Dictionary dictionary) {
    if (dictionary instanceof ShaleiaDictionary) {
      if (dictionary.getVersion() == "5.5") {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }

  public String getName() {
    return NAME
  }

  public KeyCode getKeyCode() {
    return KeyCode.U
  }

  public Image getIcon() {
    return null
  }

}