package eu.lod2.hooks.contexts.dataset;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;
import org.openrdf.model.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * The AtContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.AtCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.AtUpdateHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class AtContext extends Context {

  //--- CONSTRUCTORS

  /**
   * Constructs a new AtContext with all variables set.
   */
  public AtContext( Catalog catalog, HttpServletRequest request, Model statements, URI datasetUri ){
    this.catalog = catalog;
    this.request = request;
    this.statements = statements;
    this.datasetUri = datasetUri;
  }

  //--- GETTERS AND SETTERS

  /**
   * The {@link eu.lod2.edcat.model.Catalog} on which this request operates.
   */
  private Catalog catalog;

  /**
   * Returns the catalog on which this request operates.
   */
  public Catalog getCatalog() {
    return catalog;
  }

  /**
   * Request as sent by the user.
   * May be consulted to get request information.
   */
  private HttpServletRequest request;

  /**
   * Request as sent by the user.
   * May be consulted to get request information.
   */
  public HttpServletRequest getRequest() {
    return request;
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
