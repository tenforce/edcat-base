package eu.lod2.hooks.contexts.distribution;

import eu.lod2.hooks.contexts.base.PostContextBase;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * The PostContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.distribution.PostCreateHandler}*
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class PostContext implements PostContextBase {
  private URI distributionUri;
  private URI datasetUri;
  private Model statements;
  private HttpServletRequest request;
  private ResponseEntity<Object> response;


  //--- CONSTRUCTORS

  /**
   * Constructs a new PostContext with all variables set.
   */
  public PostContext(HttpServletRequest request, ResponseEntity<Object> response, URI datasetUri, URI distributionUri, Model statements){
    this.request = request;
    this.response = response;
    this.datasetUri = datasetUri;
    this.statements = statements;
    this.distributionUri = distributionUri;
  }

  //--- GETTERS AND SETTERS

  /**
   * Request as sent by the user.
   * May be consulted to get request information.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

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
   * Retrieves the statements which will be created in this action.
   */
   public Model getStatements() {
     return statements;
   }

  /**
   * Returns the URI of the distribution
   */
  public URI getDistributionUri() {
    return distributionUri;
  }
}
