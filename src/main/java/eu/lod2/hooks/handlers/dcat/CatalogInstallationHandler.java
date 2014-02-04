package eu.lod2.hooks.handlers.dcat;

import eu.lod2.hooks.contexts.CatalogInstallationContext;

/**
 * This handler will be called when the system is being set up.
 *
 * Plugins may use this hook for handling their setup at installation time.
 */
@SuppressWarnings( {"UnusedDeclaration"} )
public interface CatalogInstallationHandler {

  /**
   * Called just after a new CatalogService is created in the DCAT store.
   *
   * Use this if you need to configure your plugin to handle a new CatalogService.
   *
   * @param context The context provides all information which you have available to you during
   *                the installation of a new CatalogService.
   */
  public void handleCatalogInstall( CatalogInstallationContext context ) throws ActionAbortException;

}