package eu.lod2.hooks.handlers.dcat;

import eu.lod2.hooks.contexts.InstallationContext;
import eu.lod2.hooks.handlers.HookHandler;

/**
 * This handler will be called when the system is being set up.
 *
 * Plugins may use this hook for handling their setup at installation time.
 */
@SuppressWarnings( "UnusedDeclaration" )
public interface InstallationHandler extends HookHandler {

  /**
   * Called just after the installation of the core system is performed.
   *
   * Use this if you need to setup parts of your plugin during installation time.
   *
   * @param context The context provides all information which you have available to you during
   *                the installation of your plugin.
   */
  public void handleInstall( InstallationContext context ) throws ActionAbortException;
}
