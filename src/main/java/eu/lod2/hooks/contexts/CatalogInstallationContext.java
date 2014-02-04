package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.SparqlEngine;
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
   * @param engine AccessPoint to the RDF store where the CatalogService information has been stored.
   * @param catalogURI URI containing the newly inserted catalog.
   */
  @SuppressWarnings( {"UnusedDeclaration"} )
  public CatalogInstallationContext( SparqlEngine engine, URI catalogURI ) {
    this.engine = engine;
    this.catalogURI = catalogURI;
  }

  // --- ACCESSORS

  /** Contains an access point to the RDF store */
  private SparqlEngine engine;

  /**
   * Retrieves an access point to the RDF store.
   *
   * @return SparqlEngine which supplies access to the used RDF store.
   */
  @SuppressWarnings( {"UnusedDeclaration"} )
  public SparqlEngine getEngine() {
    return engine;
  }


  /** Contains the URI of the newly created CatalogService */
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