package eu.lod2.hooks.contexts.catalog;


import org.openrdf.model.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PreCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PreReadHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
public class PreContext extends Context {

  /**
   * Constructs a new PreContext with all variables set.
   */
  public PreContext( HttpServletRequest request , URI catalogUri){
    setRequest( request );
    setCatalogUri( catalogUri );
  }

  /** Return the received request from the controller. May be altered to change the request. */
  private HttpServletRequest request;

  /**
   * Returns the received request from the controller. May be altered by plugins to change the
   * request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Sets the request object on which this request operates.
   *
   * @param request Request-object on which the hook operates.
   */
  private void setRequest( HttpServletRequest request ) {
    this.request = request;
  }

  /** URI of the catalog on which this request operates */
  private URI catalogUri;

  /**
   * Retrieves the URI of the catalog on which this request operates.
   *
   * @return URI of the catalog.
   */
  public URI getCatalogUri(){
    return catalogUri;
  }

  /**
   * Sets the URI of the Catalog on which this request operates.
   *
   * @param catalogUri URI of the catalog.
   */
  protected void setCatalogUri( URI catalogUri){
    this.catalogUri = catalogUri;
  }

}