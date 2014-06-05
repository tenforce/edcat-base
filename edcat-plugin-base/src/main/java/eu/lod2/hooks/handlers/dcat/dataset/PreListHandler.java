package eu.lod2.hooks.handlers.dcat.dataset;

import eu.lod2.hooks.contexts.dataset.PreListContext;

/**
 * Implement if you are a provider for the PreListHook for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.dataset.PreListHandler"}.
 */
public interface PreListHandler {

  /**
   * Called before a LIST action on datasets is executed.
   * <p/>
   * This hook allows you to alter the request or possibly abort the action.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @see eu.lod2.hooks.contexts.dataset.PreListContext
   */
  @SuppressWarnings( "UnusedDeclaration" )
  public void handlePreList( PreListContext context );

}
