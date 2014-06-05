package eu.lod2.hooks.handlers.dcat.catalog;

import eu.lod2.hooks.contexts.catalog.AtContext;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;
import org.springframework.stereotype.Service;

/**
 * Implement if you are a provider for the AtCreateHook for Catalogs.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.catalog.AtCreateHandler"}.
 */
@Service("CatalogAtCreateHandler")
@SuppressWarnings( "UnusedDeclaration" )
public interface AtCreateHandler extends HookHandler {

  /**
   * Called just before a CREATE action on a Catalog is solidified in the database.
   * <p/>
   * This hook allows you to modify and extend the description of the Catalog.
   *
   * @param context Contains all information the consumer provides to this provider.
   * @exception ActionAbortException Throwing this exception will abort the CREATE action.
   * @see eu.lod2.hooks.contexts.catalog.AtContext
   */
  public void handleAtCreate(AtContext context) throws ActionAbortException;
}
