package eu.lod2.hooks.handlers.dcat.catalog;


import eu.lod2.hooks.contexts.catalog.PostContext;
import eu.lod2.hooks.handlers.HookHandler;


/**
 * Implement if you are a provider for the PostUpdateHook for Catalogs.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.catalog.PostUpdateHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface PostUpdateHandler extends HookHandler {

  /**
   * Called after an UPDATE action on a Catalog is finished, yet before the response is
   * returned to the user.
   * <p/>
   * This hook allows you to clean up and monitor a successful update.  It also allows
   * you to alter the response. You are *not* allowed to abort the action, as the results
   * have already been committed.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @see eu.lod2.hooks.contexts.catalog.PostContext
   */
  public void handlePostUpdate(PostContext context);
}
