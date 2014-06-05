package eu.lod2.hooks.handlers.dcat.distribution;

import eu.lod2.hooks.contexts.distribution.PostListContext;
import eu.lod2.hooks.handlers.HookHandler;
import org.springframework.stereotype.Service;

/**
 * Implement if you are a provider for the PostListHook for Distributions.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.distribution.PostListHandler"}.
 */
@Service("DistributionPostListHandler")
public interface PostListHandler extends HookHandler {

  /**
   * Called after a LIST action on a Distribution is finished, yet before the response is
   * returned to the user.
   * <p/>
   * This hook allows you to clean up and monitor a successful LIST.  It also allows
   * you to alter the response. You are *not* allowed to abort the action at this stage.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @see eu.lod2.hooks.contexts.distribution.PostListContext
   */
  @SuppressWarnings( "UnusedDeclaration" )
  public void handlePostList(PostListContext context);

}
