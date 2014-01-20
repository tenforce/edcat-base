package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.SparqlEngine;
import org.openrdf.model.Model;

/**
 * The AtContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.AtCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.AtUpdateHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class AtContext extends Context {

  //--- CONSTRUCTORS

  /**
   * Constructs a new PreContext with all fields set.
   */
  public AtContext( Catalog catalog, Model statements, SparqlEngine engine ){
    this.catalog = catalog;
    this.statements = statements;
    this.engine = engine;
  }

  //--- GETTERS AND SETTERS

  /**
   * The {@link eu.lod2.edcat.utils.Catalog} on which this request operates.
   */
  private Catalog catalog;

  /**
   * Returns the catalog on which this request operates.
   */
  public Catalog getCatalog() {
    return catalog;
  }

  /**
   * Contains the RDF statements which describe the DataSet on which the
   * request operates.
   */
  private Model statements;

  /**
   * Returns the RDF statements which describe the DataSet on which this
   * request operates.
   */
  public Model getStatements() {
    return statements;
  }

  /**
   * Engine used for retrieving and inserting data in the RDF store.
   */
  private SparqlEngine engine;

  /**
   * Returns the engine used for retrievig and inserting data in the
   * RDF store.
   */
  public SparqlEngine getEngine() {
    return engine;
  }
}
