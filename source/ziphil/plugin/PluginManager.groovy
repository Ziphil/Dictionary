package ziphil.plugin

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class PluginManager {

  public static final List<Plugin> PLUGINS = loadPlugins()

  private static List<Plugin> loadPlugins() {
    List<Plugin> plugins = ArrayList.new()
    ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin, Thread.currentThread().getContextClassLoader())
    for (Plugin plugin : loader) {
      plugins.add(plugin)
    }
    return plugins
  }

}