package eu.lod2.hooks.util;

/**
 * This error is thrown when a hook is called which has more than one implementation for the hook's
 * method.  Such behavior is currently not allowed due to Java reflection limitations.
 */
public class MultiImplementedHookException extends Exception {

  /**
   * Simple constructor for the MultiImplementedHookException
   *
   * @param handlerMethodName Name of the method for which the hook had multiple implementations.
   */
  public MultiImplementedHookException( String handlerMethodName ) {
    super( "Hook has multiple implementations for " + handlerMethodName + " which is not allowed." );
  }
}
