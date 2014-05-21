package eu.lod2.hooks.contexts;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;

/**
 * The PostContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.PostReadHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.PostCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.PostUpdateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.PostDestroyHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class PostContext {

  //--- CONSTRUCTORS

  /**
   * Constructs a new PostContext with all variables set.
   */
  public PostContext( Catalog catalog, ResponseEntity<Object> response, URI datasetUri, Model statements ){
    this.catalog = catalog;
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
