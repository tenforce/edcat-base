package eu.lod2.hooks.handlers.dcat;


import eu.lod2.hooks.contexts.AtContext;
import eu.lod2.hooks.handlers.HookHandler;


/**
 * Implement if you are a provider for the AfterCreateHook for DataSets.
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.hooks.handlers.dcat.AtCreateHandler"}.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface AtCreateHandler extends HookHandler {

  /**
   * Called just before a CREATE action on a DataSet is solidified in the database.
   * <p/>
   * This hook allows you to modify and extend the description of the DataSet.
   *
   * @param context Contains all information the consumer provides to this provider.
   * @exception ActionAbortException Throwing this exception will abort the CREATE action.
   * @see eu.lod2.hooks.contexts.AtContext
   */
  public void handleAtCreate(AtContext context) throws ActionAbortException;
}
