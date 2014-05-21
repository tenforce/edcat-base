package eu.lod2.hooks.contexts.distribution;

import eu.lod2.edcat.model.Catalog;
import eu.lod2.hooks.contexts.Context;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;

/**
 * The PostContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.distribution.PostCreateHandler}*
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class PostContext extends Context {
  private URI distributionUri;
  private URI datasetUri;
  private Model dataset;
  private ResponseEntity<Object> response;


  //--- CONSTRUCTORS

  /**
   * Constructs a new PostContext with all variables set.
   */
  public PostContext(ResponseEntity<Object> response, URI datasetUri, URI distributionUri, Model statements){
    this.response = response;
    this.datasetUri = datasetUri;
    this.dataset = statements;
    this.distributionUri = distributionUri;
  }

  //--- GETTERS AND SETTERS

  /**
   * Response as sent to the User.
   * May be altered to change the response.
   */
  public ResponseEntity<Object> getResponse() {
    return response;
  }

  /**
   * Returns the URI of the DataSet.
   */
  public URI getDatasetUri() {
    return datasetUri;
  }

  /**
   * Returns the dataset statements
   */
   public Model getDataset() {
     return dataset;
   }

  /**
   * Returns the URI of the distribution
   */
  public URI getDistributionUri() {
    return distributionUri;
  }
}
