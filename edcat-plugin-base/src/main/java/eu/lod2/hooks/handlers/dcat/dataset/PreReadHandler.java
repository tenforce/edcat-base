package eu.lod2.hooks.handlers.dcat.dataset;


import eu.lod2.hooks.contexts.dataset.PreContext;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;


/**
 * Implement if you are a provider for the PreReadHandler for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.dataset.PreReadHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface PreReadHandler extends HookHandler {

  /**
   * Called before a READ action for a DataSet is constructed.
   * <p/>
   * This hook allows you to alter the request or possibly abort the action.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @throws eu.lod2.hooks.handlers.dcat.ActionAbortException Throwing this exception will abort the READ action.
   * @see eu.lod2.hooks.contexts.dataset.PreContext
   */
  public void handlePreRead(PreContext context) throws ActionAbortException;
}
