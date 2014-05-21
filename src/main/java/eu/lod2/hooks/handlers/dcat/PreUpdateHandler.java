package eu.lod2.hooks.handlers.dcat;


import eu.lod2.hooks.contexts.PreContext;
import eu.lod2.hooks.handlers.HookHandler;


/**
 * Implement if you are a provider for the PreUpdateHandler for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.PreUpdateHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface PreUpdateHandler extends HookHandler {

  /**
   * Called before an UPDATE action for a DataSet is constructed.
   * <p/>
   * This hook allows you to alter the request or possibly abort the action.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @throws ActionAbortException Throwing this exception will abort the UPDATE action.
   * @see eu.lod2.hooks.contexts.PreContext
   */
  public void handlePreUpdate(PreContext context) throws ActionAbortException;
}
