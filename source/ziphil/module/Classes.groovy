package ziphil.module

import groovy.transform.CompileStatic
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Classes {

  public static Class<?> determineGenericType(Class<?> firstClass, Class<?> targetClass, String typeParameterName) {
    Deque<Class<?>> classChain = searchTargetClass(firstClass, targetClass, ArrayDeque.new())
    if (classChain != null) {
      return searchGenericType(firstClass, typeParameterName, classChain)
    } else {
      return null
    }
  }

  private static Class<?> searchGenericType(Class<?> clazz, String typeParameterName, Deque<Class<?>> classChain) {
    return null
  }

  private static Deque<Class<?>> searchTargetClass(Class<?> clazz, Class<?> targetClass, ArrayDeque<Class<?>> classChain) {
    classChain.push(clazz)
    if (clazz == targetClass) {
      return classChain
    } else {
      Class<?> superclass = clazz.getSuperclass()
      if (superclass != null) {
        Deque<Class<?>> resultClassChain = searchTargetClass(superclass, targetClass, classChain.clone())
        if (resultClassChain != null) {
          return resultClassChain
        }
      }
      for (Class<?> nextInterface : clazz.getInterfaces()) {
        Deque<Class<?>> resultClassChain = searchTargetClass(nextInterface, targetClass, classChain.clone())
        if (resultClassChain != null) {
          return resultClassChain
        }
      }
      return null
    }
  }

}