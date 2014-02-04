package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.CatalogService;
import eu.lod2.edcat.utils.SparqlEngine;
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
  public PostContext( CatalogService catalogService, ResponseEntity<Object> response, SparqlEngine engine, URI datasetUri, Model statements ){
    this.catalogService = catalogService;
    this.engine = engine;
    this.response = response;
    this.datasetUri = datasetUri;
    this.dataset = statements;
  }

  //--- GETTERS AND SETTERS

  /**
   * The {@link eu.lod2.edcat.utils.CatalogService} on which this request
   * operates.
   */
  private CatalogService catalogService;

  /**
   * Returns the CatalogService on which this request operates.
   */
  public CatalogService getCatalogService() {
    return catalogService;
  }

  /**
   * The SparqlEngine used for fulfilling the request.
   */
  private SparqlEngine engine;

  /**
   * Engine used for retrieving and inserting data in the RDF store.
   */
  public SparqlEngine getEngine() {
    return engine;
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
