package eu.lod2.hooks.contexts;

import org.openrdf.model.URI;

/**
 * This context contains the information available to a plugin which hooks into the creation of an
 * individual catalog.
 */
public class CatalogInstallationContext extends Context {

  // --- CONSTRUCTORS

  /**
   * Constructs a new CatalogInstallationContext supplying all necessary information.
   *
   * @param catalogURI URI containing the newly inserted catalog.
   */
  @SuppressWarnings( {"UnusedDeclaration"} )
  public CatalogInstallationContext( URI catalogURI ) {
    this.catalogURI = catalogURI;
  }

  // --- ACCESSORS

  /** Contains the URI of the newly created Catalog */
  private URI catalogURI;

  /**
   * Retrieves the URI of the newly created catalog.
   *
   * @return URI which represents the identifier for the catalog and which is used to contain the
   * graph of the store.
   */
  @SuppressWarnings( {"UnusedDeclaration"} )
  public URI getCatalogURI() {
    return catalogURI;
  }

}