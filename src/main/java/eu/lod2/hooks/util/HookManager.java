package eu.lod2.hooks.util;

import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.constraints.graph.HookHandlerNodeSet;
import eu.lod2.hooks.constraints.graph.NodeSet;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.OptionalHookHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;

public class HookManager {

  static ServiceLoader<HookHandler> handlers = ServiceLoader.load(HookHandler.class);

  private static List<HookHandler> getHandlerFor(Class<?> hookInterface) {
    List<HookHandler> filteredHandlers = new ArrayList<HookHandler>();
    for (HookHandler handler : handlers) {

      List<Class<?>> classes = getAllInterfaces(handler.getClass());
      if (classes.contains(hookInterface) || optionalHookHandlerImplements(handler, hookInterface))
        filteredHandlers.add(handler);
    }
    return filteredHandlers;
  }

  private static List<HookHandler> prioritySort(Collection<HookHandler> hookHandlers, String hook) throws CycleException {
    NodeSet<HookHandler> nodeSet = new HookHandlerNodeSet(hookHandlers, hook);
    if (nodeSet.hasCycleP())
      throw new CycleException();
    return nodeSet.handlersExecutionList();
  }

  private static boolean optionalHookHandlerImplements(HookHandler handler, Class<?> hookInterface) {
    return handler instanceof OptionalHookHandler && ((OptionalHookHandler) handler).isHandlingHook(hookInterface.getCanonicalName());
  }

  public static List<HookHandler> orderedHandlers(Class<?> hook) throws ClassNotFoundException, CycleException {
    return prioritySort(getHandlerFor(hook), hook.getCanonicalName());
  }

  /**
   *   NOTE: the code below was extract from Apache Commons Lang (3.1)
   *   It is Copyright 2001-2011 The Apache Software Foundation
   *   TODO: depend on commons-lang?
   */

  /**
   * <p>Gets a {@code List} of all interfaces implemented by the given
   * class and its superclasses.</p>
   * <p/>
   * <p>The order is determined by looking through each interface in turn as
   * declared in the source file and following its hierarchy up. Then each
   * superclass is considered in the same way. Later duplicates are ignored,
   * so the order is maintained.</p>
   *
   * @param cls the class to look up, may be {@code null}
   * @return the {@code List} of interfaces in order,
   *         {@code null} if null input
   */
  public static List<Class<?>> getAllInterfaces(Class<?> cls) {
    if (cls == null) {
      return null;
    }

    LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
    getAllInterfaces(cls, interfacesFound);

    return new ArrayList<Class<?>>(interfacesFound);
  }

  /**
   * Get the interfaces for the specified class.
   *
   * @param cls             the class to look up, may be {@code null}
   * @param interfacesFound the {@code Set} of interfaces for the class
   */
  private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
    while (cls != null) {
      Class<?>[] interfaces = cls.getInterfaces();

      for (Class<?> i : interfaces) {
        if (interfacesFound.add(i)) {
          getAllInterfaces(i, interfacesFound);
        }
      }

      cls = cls.getSuperclass();
    }
  }


  /**
   * Calls a hook, given the basic information about it.
   * <p/>
   * This handles the bookkeeping regarding the ordering of handlers and abstracts out the difference
   * between {@link OptionalHookHandler}s and the handlerInterface.
   *
   * @param handlerInterface  The interface by which the hook is specified.
   * @param handlerMethodName The method to call on the supplied interface.
   * @param args              The arguments to supply to the hook's implementer.
   */
  public static void callHook(Class handlerInterface, String handlerMethodName, Object... args) throws CycleException, ClassNotFoundException {
    for (HookHandler h : orderedHandlers(handlerInterface))
//            if (handlerInterface.isInstance(handlerInterface)) {
      if (h instanceof OptionalHookHandler)
        // todo: splash the arguments
        ((OptionalHookHandler) h).handle(handlerInterface.getCanonicalName(), args);
      else {
        Class[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++)
          argsClasses[i] = args[i].getClass();
        try {

          Method hookMethod = h.getClass().getMethod(handlerMethodName, argsClasses);
//                      Method hookMethod  = ClassUtils.getPublicMethod(h.getClass(), handlerMethodName, argsClasses);
          hookMethod.invoke(h, args);
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
//            }
  }
}
