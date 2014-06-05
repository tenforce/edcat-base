package eu.lod2.hooks.contexts.distribution;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreListContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.distribution.PreListHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
public class PreListContext extends Context {

  /**
   * Simple constructor for the PreListContext.
   */
  public PreListContext(HttpServletRequest request) {
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
