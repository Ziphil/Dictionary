package ziphil.dictionary

import groovy.transform.CompileStatic
import java.security.AccessController
import java.security.AccessControlContext
import java.security.CodeSource
import java.security.Permissions
import java.security.PrivilegedExceptionAction
import java.security.ProtectionDomain
import java.security.cert.Certificate
import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.ScriptException
import ziphil.module.NoSuchScriptEngineException
import ziphil.module.Setting
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class ScriptSearchParameter implements SearchParameter<Word> {

  private static final AccessControlContext ACCESS_CONTROL_CONTEXT = createAccessControlContext()

  private String $script = ""
  private String $scriptName
  private ScriptEngine $scriptEngine
  private Dictionary $dictionary

  public ScriptSearchParameter(String script) {
    $script = script
  }

  public void preprocess(Dictionary dictionary) {
    $scriptName = Setting.getInstance().getScriptName()
    $scriptEngine = ScriptEngineManager.new().getEngineByName($scriptName)
    $dictionary = dictionary
  }

  public Boolean matches(Word word) {
    if ($scriptEngine != null) {
      Exception suppressedException = null
      PrivilegedExceptionAction<BooleanClass> action = (PrivilegedExceptionAction){
        try {
          if (suppressedException == null) {
            $scriptEngine.put("word", $dictionary.createPlainWord(word))
            Object result = $scriptEngine.eval($script)
            return (result) ? true : false
          } else {
            return false
          }
        } catch (Exception exception) {
          suppressedException = exception
          return false
        }
      }
      Boolean predicate = AccessController.doPrivileged(action, ACCESS_CONTROL_CONTEXT)
      if (suppressedException != null) {
        throw suppressedException
      }
      return predicate
    } else {
      throw NoSuchScriptEngineException.new($scriptName)
    }
  }

  public String toString() {
    return "スクリプト"
  }

  private static AccessControlContext createAccessControlContext() {
    CodeSource codeSource = CodeSource.new(null, (Certificate[])null)
    Permissions permissions = Permissions.new()
    permissions.add(PropertyPermission.new("*", "read"))
    permissions.add(RuntimePermission.new("accessDeclaredMembers"))
    permissions.add(RuntimePermission.new("createClassLoader"))
    permissions.add(RuntimePermission.new("getProtectionDomain"))
    ProtectionDomain domain = ProtectionDomain.new(codeSource, permissions)
    ProtectionDomain[] domains = [domain]
    AccessControlContext context = AccessControlContext.new(domains)
    return context
  }

  public String getScript() {
    return $script
  }

  public void setScript(String script) {
    $script = script
  }

}