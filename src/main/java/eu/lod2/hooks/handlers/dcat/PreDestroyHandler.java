package eu.lod2.hooks.handlers.dcat;

import eu.lod2.hooks.contexts.PreContext;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.util.ActionAbortException;

/**
 * Implement if you are a provider for the PreDestroyHook for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.PreDestroyHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface PreDestroyHandler extends HookHandler {

  /**
   * Called before a DESTROY action is performed on a DataSet.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @throws ActionAbortException Throwing this exception will abort the DESTROY action.
   */
  public void handlePreDestroy(PreContext context) throws ActionAbortException;
}
