package eu.lod2.edcat.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HookManager {

  @Autowired
  static List<HookHandler> handlers = new ArrayList<HookHandler>();

  private static List<HookHandler> getHandlerFor(Class<?> hookInterface) {
    List<HookHandler> filteredHandlers = new ArrayList<HookHandler>();
    for (HookHandler handler : handlers) {
      List<Class> classes = Arrays.asList(ClassUtils.getAllInterfaces(handler.getClass()));
      if (classes.contains(hookInterface) || optionalHookHandlerImplements(handler,hookInterface) )
        filteredHandlers.add(handler);
    }
    return filteredHandlers;
  }

  private static List<HookHandler> prioritySort(List<HookHandler> hookHandlers,String hook) {
    // TODO: actually sort it
    return hookHandlers;
  }

  private static boolean optionalHookHandlerImplements(HookHandler handler,Class<?> hookInterface) {
   return  handler instanceof OptionalHookHandler && ((OptionalHookHandler) handler).isHandlingHook(hookInterface.getCanonicalName());
  }

  public static List<HookHandler> orderedHandlers(Class<?> hook) throws ClassNotFoundException {
    return prioritySort(getHandlerFor(hook),hook.getCanonicalName());
  }



}
