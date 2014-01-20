package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.SparqlEngine;

/**
 * The InstallationContext is used for:
 * - {@link eu.lod2.hooks.handlers.dcat.InstallationHandler}
 */
@SuppressWarnings( "UnusedDeclaration" )
public class InstallationContext extends Context {

  /** Contains an engine for accessing the RDF store. */
  private SparqlEngine engine;

  /**
   * Constructs a new InstallationContext for use in an
   * {@link eu.lod2.hooks.handlers.dcat.InstallationHandler}
   *
   * @param engine Can be used for executing queries on the RDF store.
   */
  public InstallationContext(SparqlEngine engine){
    this.engine = engine;
  }

  /**
   * Returns an engine which may be used to execute queries on the RDF store.
   *
   * @return SparqlEngine for executing queries on the RDF store.
   */
  public SparqlEngine getEngine(){
    return engine;
  }

}
