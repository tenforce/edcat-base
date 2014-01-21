package eu.lod2.hooks.handlers.dcat;

import eu.lod2.hooks.contexts.PostListContext;

/**
 * Implement if you are a provider for the PostListHook for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.PostListHandler"}.
 */
public interface PostListHandler {

  /**
   * Called before a LIST action on a Catalog is executed.
   * <p/>
   * This hook allows you to clean up and monitor a successful LIST.  It also allows
   * you to alter the response. You are *not* allowed to abort the action at this stage.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @see eu.lod2.hooks.contexts.PostListContext
   */
  @SuppressWarnings( "UnusedDeclaration" )
  public void handlePostList( PostListContext context );

}
