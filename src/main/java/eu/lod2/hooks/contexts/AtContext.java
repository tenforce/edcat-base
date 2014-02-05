package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.CatalogService;
import org.openrdf.model.Model;
import org.openrdf.model.URI;

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
  public AtContext( CatalogService catalogService, Model statements, URI datasetUri ){
    this.catalogService = catalogService;
    this.statements = statements;
    this.datasetUri = datasetUri;
  }

  //--- GETTERS AND SETTERS

  /**
   * The {@link eu.lod2.edcat.utils.CatalogService} on which this request operates.
   */
  private CatalogService catalogService;

  /**
   * Returns the catalogService on which this request operates.
   */
  public CatalogService getCatalogService() {
    return catalogService;
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

  /** URI identifier for the DataSet which will be created. */
  private URI datasetUri;

  /**
   * Returns the URI identifier for the DataSet which will be created.
   * <p/>
   * This URI serves both as the name of the graph in which the metadata of the DataSet is stored,
   * as the name by which we identify the DataSet in the config graph.
   *
   * @return URI identifying the DataSet.
   */
  public URI getDatasetUri(){
    return datasetUri;
  }

}
