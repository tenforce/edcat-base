package eu.lod2.hooks.handlers.dcat.dataset;

import eu.lod2.hooks.contexts.dataset.PostContext;
import eu.lod2.hooks.handlers.HookHandler;

/**
 * Implement if you are a provider for the PostReadHook for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.dataset.PostReadHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface PostReadHandler extends HookHandler {

  /**
   * Called after a READ action on a DataSet is finished, yet before the response is
   * returned to the user.
   * <p/>
   * This hook allows you to clean up and monitor a successful READ.  It also allows
   * you to alter the response. You are *not* allowed to abort the action at this stage.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @see eu.lod2.hooks.contexts.dataset.PostContext
   */
  public void handlePostRead(PostContext context);
}
