package eu.lod2.hooks.contexts.distribution;

import org.openrdf.model.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreContext is used for:
 * <p/>
 * {@link eu.lod2.hooks.handlers.dcat.distribution.PreCreateHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
@SuppressWarnings("UnusedDeclaration")
public class PreContext extends Context {
  private URI datasetUri;
  private URI distributionUri;
  private HttpServletRequest request;


  //--- CONSTRUCTORS

  /**
   * Constructs a new PreContext with all variables set.
   */
  public PreContext(HttpServletRequest request, URI datasetUri, URI distributionUri) {
    this.request = request;
    this.datasetUri = datasetUri;
    this.distributionUri = distributionUri;
  }



  //--- GETTERS AND SETTERS


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

  /**
   * Returns the received request from the controller. May be altered by plugins to change the
   * request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  public URI getDistributionUri() {
    return distributionUri;
  }

  public void setDistributionUri(URI distributionUri) {
    this.distributionUri = distributionUri;
  }

}
