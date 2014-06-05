package eu.lod2.hooks.contexts.dataset;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreListContext is used for {@link eu.lod2.hooks.handlers.dcat.dataset.PreListHandler} and may be
 * used by other hooks which communicate similar information.
 */
public class PreListContext extends Context {

  /**
   * Constructs a new PreListContext with all fields set.
   */
  public PreListContext( HttpServletRequest request ) {
    this.request = request;
  }

  /**
   * Return the received request from the controller. May be altered to change the request.
   */
  private HttpServletRequest request;

  /**
   * Returns the received request from the controller. May be altered by plugins to change the
   * request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

}
