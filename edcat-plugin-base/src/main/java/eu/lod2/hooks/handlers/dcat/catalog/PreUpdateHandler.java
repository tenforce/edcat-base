package eu.lod2.hooks.handlers.dcat.catalog;


import eu.lod2.hooks.contexts.catalog.PreContext;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;


/**
 * Implement if you are a provider for the PreUpdateHandler for Catalogs.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.catalog.PreUpdateHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface PreUpdateHandler extends HookHandler {

  /**
   * Called before an UPDATE action for a Catalog is constructed.
   * <p/>
   * This hook allows you to alter the request or possibly abort the action.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @throws eu.lod2.hooks.handlers.dcat.ActionAbortException Throwing this exception will abort the UPDATE action.
   * @see eu.lod2.hooks.contexts.catalog.PreContext
   */
  public void handlePreUpdate(PreContext context) throws ActionAbortException;
}
