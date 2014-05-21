package eu.lod2.hooks.contexts.catalog;

import javax.servlet.http.HttpServletRequest;

/**
 * The AtContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PreListHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
public class PreListContext extends Context {

  /**
   * Simple constructor for the PostListContext.
   */
  public PreListContext( HttpServletRequest request ) {
    setRequest( request );
  }

  /** Contains the request which was made for the current action. */
  private HttpServletRequest request;

  /**
   * Sets the request which was made for the current action.
   *
   * @param request Request which has been made.
   */
  protected void setRequest( HttpServletRequest request ) {
    this.request = request;
  }

  /**
   * Retrieves the request which was made to initiate the current action.
   *
   * @return Request which was made.
   */
  public HttpServletRequest getRequest() {
    return this.request;
  }

}
