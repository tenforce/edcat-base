package eu.lod2.hooks.util;

import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.constraints.graph.HookHandlerNodeSet;
import eu.lod2.hooks.constraints.graph.NodeSet;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.OptionalHookHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;

/**
 * The HookManager is your one-stop service for calling hooks.
 * <p/>
 * You can call a hook by using the {@link #callHook(Class, String, Object...)} method.
 * <p/>
 * <code>
 *   HookManager.callHook(AtCreateHandler.class, "handleAtCreate", 1, 2, new Tree()
 * </code>
 * <p/>
 * Would execute the handleAtCreate method of all hooks which implement the AtCreateHandler class
 * (and which have registered them) with the arguments {@code 1}, {@code 2} and {@code new Tree()}.
 * It will also handle all providers of {@link eu.lod2.hooks.handlers.OptionalHookHandler}.  Each of
 * the handlers will be called in an order which abides their priority constraints as good as in a
 * best-effort manner.
 */
public class HookManager {

  /** Contains all {@code HookHandler}s which have registered themselves for loading */
  static ServiceLoader<HookHandler> handlers = ServiceLoader.load( HookHandler.class );

  /**
   * Searches for all providers which implement the given {@link HookHandler} interface. The
   * returned handlers have *not* been sorted by priority yet.
   *
   * @param hookInterface Interface which the {@link HookHandler} should implement.
   * @return Collection of all providers which implement {@link HookHandler}.
   */
  private static Collection<HookHandler> getHandlersFor( Class<?> hookInterface ) {
    List<HookHandler> filteredHandlers = new ArrayList<HookHandler>();
    for ( HookHandler handler : handlers ) {

      List<Class<?>> classes = Apache.getAllInterfaces( handler.getClass() );
      if ( classes.contains( hookInterface ) || optionalHookHandlerImplements( handler, hookInterface ) )
        filteredHandlers.add( handler );
    }
    return filteredHandlers;
  }

  /**
   * Sorts a collection of {@link HookHandler}s by their priority for being called by the hook
   * function with name {@code hook}.  In the returned {@link List} of {@link HookHandler}s, the
   * handler which is named first should be called before the {@link HookHandler}s which appear
   * later in the list.
   * <p/>
   * Each {@link HookHandler} in {@code hookHandlers} is assumed to have implemented the priority
   * functionality for {@code hook}.  If the priorities of the {@code hookHandler} has cycles in it,
   * a {@link CycleException} is thrown.
   *
   * @param hookHandlers Collection of HookHandlers to be sorted.
   * @param hook         Name of the method for which we are doing the sorting.
   * @return {@code hookHandler}s sorted by the priority in which they should be executed.
   * @throws CycleException Thrown if there is a cycle in the dependencies of the supplied {@code
   *                        hookHandlers} for {@code hook}.
   * @see eu.lod2.hooks.handlers.HookHandler
   */
  private static List<HookHandler> prioritySort( Collection<HookHandler> hookHandlers, String hook ) throws CycleException {
    NodeSet<HookHandler> nodeSet = new HookHandlerNodeSet( hookHandlers, hook );
    if ( nodeSet.hasCycleP() )
      throw new CycleException();
    return nodeSet.handlersExecutionList();
  }

  /**
   * Returns true iff {@code handler} is a {@link eu.lod2.hooks.handlers.OptionalHookHandler} which
   * acts as a provider for {@code hookInterface}.  You can find more information about optional
   * hook handlers in the documentation of {@link eu.lod2.hooks.handlers.HookHandler} and in the
   * documentation of {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
   *
   * @param handler       HookHandler for which we want to check if it implements {@code
   *                      hookInterface} as an optional handler.
   * @param hookInterface Interface for which we want to search the {@link OptionalHookHandler}
   *                      providers.
   * @return true iff {@code handler} implements {@code hookInterface} as an optional handler.
   */
  private static boolean optionalHookHandlerImplements( HookHandler handler, Class<?> hookInterface ) {
    return handler instanceof OptionalHookHandler && (( OptionalHookHandler ) handler).isHandlingHook( hookInterface.getCanonicalName() );
  }

  /**
   * Returns an {@link List} of all providers for {@code hook}, ordered by the priority of their
   * execution.
   *
   * @param hook Subclass of {@link eu.lod2.hooks.handlers.HookHandler} for which we want to
   *             retrieve the providers ordered by their execution.
   * @return Providers for {@code hook} ordered by preferred execution time.
   * @throws CycleException Throws a {@link CycleException} iff a cycle was found in the {@link
   *                        eu.lod2.hooks.constraints.Priority} constraints which providers have
   *                        placed on their execution.
   */
  private static List<HookHandler> orderedHandlers( Class<?> hook ) throws CycleException {
    return prioritySort( getHandlersFor( hook ), hook.getCanonicalName() );
  }

  /**
   * Calls a hook, given the basic information about it.
   * <p/>
   * This handles the bookkeeping regarding the ordering of handlers and abstracts out the
   * difference between {@link OptionalHookHandler}s and the handlerInterface.
   *
   * @param handlerInterface  The interface by which the hook is specified.
   * @param handlerMethodName The method to call on the supplied interface.
   * @param args              The arguments to supply to the hook's implementer.
   */
  public static void callHook( Class handlerInterface, String handlerMethodName, Object... args ) throws Throwable {
    for ( HookHandler h : orderedHandlers( handlerInterface ) )
      if ( h instanceof OptionalHookHandler )
        // todo: splash the arguments
        // (aad) isn't this handled automatically?
        (( OptionalHookHandler ) h).handle( handlerInterface.getCanonicalName(), args );
      else {
        List<Method> correctlyNamedMethods = new ArrayList<Method>();
        for ( Method m : h.getClass().getMethods() )
          if ( m.getName().equals( handlerMethodName )
              && !Modifier.isAbstract( m.getModifiers() )
              && !Modifier.isPrivate( m.getModifiers() )
              && !Modifier.isInterface( m.getModifiers() ) )
            correctlyNamedMethods.add( m );

        if ( correctlyNamedMethods.size() > 1 )
          throw new MultiImplementedHookException( handlerMethodName );
        else if ( correctlyNamedMethods.size() == 1 ) {
          try {
            Method method = correctlyNamedMethods.get( 0 );
            method.invoke( h, args );
          } catch ( IllegalAccessException e ) {
            e.printStackTrace();
          } catch ( InvocationTargetException e ) {
            throw e.getTargetException();
          }
        }
      }
  }
}

/**
 * This code was taken from Apache Commons Lang (3.1).
 * It is Copyright 2001-2011 The Apache Software Foundation
 *
 * TODO: Do we want to depend on commons-lang?
 */
class Apache {

  /**
   * <p>Gets a {@code List} of all interfaces implemented by the given class and its
   * superclasses.</p> <p/> <p>The order is determined by looking through each interface in turn as
   * declared in the source file and following its hierarchy up. Then each superclass is considered
   * in the same way. Later duplicates are ignored, so the order is maintained.</p>
   *
   * @param cls the class to look up, may be {@code null}
   * @return the {@code List} of interfaces in order, {@code null} if null input
   */
  static List<Class<?>> getAllInterfaces( Class<?> cls ) {
    if ( cls == null )
      return null;

    LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
    getAllInterfaces( cls, interfacesFound );

    return new ArrayList<Class<?>>( interfacesFound );
  }

  /**
   * Get the interfaces for the specified class.
   *
   * @param cls             the class to look up, may be {@code null}
   * @param interfacesFound the {@code Set} of interfaces for the class
   */
  static void getAllInterfaces( Class<?> cls, HashSet<Class<?>> interfacesFound ) {
    while ( cls != null ) {
      Class<?>[] interfaces = cls.getInterfaces();

      for ( Class<?> i : interfaces )
        if ( interfacesFound.add( i ) )
          getAllInterfaces( i, interfacesFound );

      cls = cls.getSuperclass();
    }
  }
}