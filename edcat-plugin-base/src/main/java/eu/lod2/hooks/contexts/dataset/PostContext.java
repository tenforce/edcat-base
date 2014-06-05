package eu.lod2.hooks.contexts.dataset;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * The PostContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.PostReadHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.PostCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.PostUpdateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.dataset.PostDestroyHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class PostContext extends Context {

  //--- CONSTRUCTORS

  /**
   * Constructs a new PostContext with all variables set.
   */
  public PostContext( Catalog catalog, HttpServletRequest request, ResponseEntity<Object> response, URI datasetUri, Model statements ){
    this.catalog = catalog;
    this.request = request;
    this.response = response;
    this.datasetUri = datasetUri;
    this.dataset = statements;
  }

  //--- GETTERS AND SETTERS

  /**
   * The {@link eu.lod2.edcat.model.Catalog} on which this request
   * operates.
   */
  private Catalog catalog;

  /**
   * Returns the Catalog on which this request operates.
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
   * Response as sent to the user.
   * May be altered to change the response.
   */
  private ResponseEntity<Object> response;

  /**
   * Response as sent to the User.
   * May be altered to change the response.
   */
  public ResponseEntity<Object> getResponse() {
    return response;
  }

  /**
   * URI of the DataSet.
   */
  private URI datasetUri;

  /**
   * Returns the URI of the DataSet.
   */
  public URI getDatasetUri() {
    return datasetUri;
  }

  /**
   * dataset statements
   */
  Model dataset;

  /**
   * Returns the dataset statements
   */
   public Model getDataset() {
     return dataset;
   }
}
