package eu.lod2.hooks.contexts.distribution;

import org.openrdf.model.Model;
import org.openrdf.model.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * The AtContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.distribution.AtCreateHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
public class AtContext extends Context {

  /** Contains the model which defines the new triples created in this AtContext. */
  private Model statements;
  private URI datasetURI;
  private URI distributionUri;
  private HttpServletRequest request;

  /**
   * Simple constructor of the AtContext.
   */
  public AtContext(HttpServletRequest request, URI dataset, URI distributionUri, Model statements) {
    setRequest(request);
    setDatasetURI(dataset);
    setStatements(statements);
    setDistributionUri(distributionUri);
  }

  /**
   * Request as sent by the user.
   * May be consulted to get request information.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Sets the request as sent by the user in this context.
   *
   * @param request Request as sent by the user.
   */
  protected void setRequest( HttpServletRequest request){
    this.request = request;
  }

  /**
   * Sets the statements of the model.
   *
   * @param statements The statements available in this AtContext.
   */
  protected void setStatements( Model statements ) {
    this.statements = statements;
  }

  /**
   * Retrieves the statements which will be created in this action.
   *
   * @return Model of this AtContext.
   */
  public Model getStatements() {
    return statements;
  }

  public URI getDatasetURI() {
    return datasetURI;
  }

  public void setDatasetURI(URI datasetURI) {
    this.datasetURI = datasetURI;
  }

  public URI getDistributionUri() {
    return distributionUri;
  }

  public void setDistributionUri(URI distributionUri) {
    this.distributionUri = distributionUri;
  }
}
