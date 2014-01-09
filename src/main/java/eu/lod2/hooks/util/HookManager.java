package eu.lod2.hooks.util;

import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.constraints.graph.HookHandlerNodeSet;
import eu.lod2.hooks.constraints.graph.NodeSet;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.OptionalHookHandler;

import java.util.*;

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
        NodeSet<HookHandler> nodeSet = new HookHandlerNodeSet( hookHandlers, hook );
        if(nodeSet.hasCycleP())
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
     * {@code null} if null input
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


}
