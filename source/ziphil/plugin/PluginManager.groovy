package ziphil.plugin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PluginManager {

  public static final List<SimplePlugin> SIMPLE_PLUGINS = loadSimplePlugins()

  private static List<SimplePlugin> loadSimplePlugins() {
    List<SimplePlugin> plugins = ArrayList.new()
    ServiceLoader<SimplePlugin> loader = ServiceLoader.load(SimplePlugin, Thread.currentThread().getContextClassLoader())
    for (SimplePlugin plugin : loader) {
      plugins.add(plugin)
    }
    return plugins
  }

}