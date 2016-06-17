package ziphil.custom

import groovy.transform.CompileStatic
import javafx.fxml.JavaFXBuilderFactory
import javafx.util.Builder
import javafx.util.BuilderFactory


@CompileStatic @Newify
public class CustomBuilderFactory implements BuilderFactory {

  private BuilderFactory $baseFactory = JavaFXBuilderFactory.new()

  @Override
  public Builder getBuilder(Class clazz) {
    if (clazz == Double) {
      return Measurement.new()
    } else {
      return $baseFactory.getBuilder(clazz)
    }
  }

}