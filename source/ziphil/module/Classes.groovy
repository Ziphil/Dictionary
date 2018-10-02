package ziphil.module

import groovy.transform.CompileStatic
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import ziphilib.transform.InnerClass
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class Classes {

  public static Class<?> determineGenericType(Class<?> firstClass, Class<?> targetClass, String targetVariableName) {
    ArrayDeque<ClassNode<?>> classChain = ArrayDeque.new()
    classChain.push(ClassNode.new(firstClass, -1))
    Deque<ClassNode<?>> resultClassChain = searchTargetClass(firstClass, targetClass, classChain)
    if (resultClassChain != null) {
      return searchGenericType(targetVariableName, resultClassChain)
    } else {
      return null
    }
  }

  private static Class<?> searchGenericType(String targetVariableName, Deque<ClassNode<?>> classChain) {
    ClassNode<?> classNode = classChain.pop()
    ClassNode<?> subclassNode = classChain.peek()
    if (subclassNode != null) {
      Class<?> clazz = classNode.getCurrentClass()
      Class<?> subclass = subclassNode.getCurrentClass()
      Int interfaceIndex = classNode.getIndex()
      Int variableIndex = 0
      Boolean found = false
      for (TypeVariable<?> variable : clazz.getTypeParameters()) {
        if (variable.getName() == targetVariableName) {
          found = true
          break
        }
        variableIndex ++
      }
      if (found) {
        Type type = (interfaceIndex >= 0) ? subclass.getGenericInterfaces()[interfaceIndex] : subclass.getGenericSuperclass()
        Type typeArgument = ((ParameterizedType)type).getActualTypeArguments()[variableIndex]
        if (typeArgument instanceof Class) {
          return (Class<?>)typeArgument
        } else if (typeArgument instanceof ParameterizedType) {
          return typeArgument.getRawType()
        } else if (typeArgument instanceof TypeVariable) {
          return searchGenericType(typeArgument.getName(), classChain)
        } else {
          return null
        }
      } else {
        return null
      }
    } else {
      return null
    }
  }

  private static Deque<ClassNode<?>> searchTargetClass(Class<?> clazz, Class<?> targetClass, ArrayDeque<ClassNode<?>> classChain) {
    Int index = 0
    Class<?> nextClass = clazz.getSuperclass()
    if (nextClass != null) {
      ArrayDeque<ClassNode<?>> nextClassChain = classChain.clone()
      nextClassChain.push(ClassNode.new(nextClass, -1))
      if (nextClass == targetClass) {
        return nextClassChain
      } else {
        Deque<ClassNode<?>> resultClassChain = searchTargetClass(nextClass, targetClass, nextClassChain)
        if (resultClassChain != null) {
          return resultClassChain
        }
      }
    }
    for (Class<?> nextInterface : clazz.getInterfaces()) {
      ArrayDeque<ClassNode<?>> nextClassChain = classChain.clone()
      nextClassChain.push(ClassNode.new(nextInterface, index))
      if (nextInterface == targetClass) {
        return nextClassChain
      } else {
        Deque<ClassNode<?>> resultClassChain = searchTargetClass(nextInterface, targetClass, nextClassChain)
        if (resultClassChain != null) {
          return resultClassChain
        }
      }
      index ++
    }
    return null
  }

}


@InnerClass(Classes)
@CompileStatic @Ziphilify
private static class ClassNode {

  private Class<?> $currentClass
  private Int $index

  public ClassNode(Class<?> currentClass, Int index) {
    $currentClass = currentClass
    $index = index
  }

  public Class<?> getCurrentClass() {
    return $currentClass
  }

  public Int getIndex() {
    return $index
  }

}