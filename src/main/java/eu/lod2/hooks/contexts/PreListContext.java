package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.SparqlEngine;

import javax.servlet.http.HttpServletRequest;

/**
 * The PreListContext is used for {@link eu.lod2.hooks.handlers.dcat.PreListHandler} and may be
 * used by other hooks which communicate similar information.
 */
public class PreListContext extends Context {

  /**
   * Constructs a new PreListContext with all fields set.
   *
   * @param request contains all information about the user's request.
   * @param engine  connection to the RDF store.
   */
  public PreListContext( HttpServletRequest request, SparqlEngine engine ) {
    this.request = request;
    this.engine = engine;
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

  /**
   * The SparqlEngine used for answering to the request.
   */
  private SparqlEngine engine;

  /**
   * Return the SparqlEngine used for answering to the request.
   */
  public SparqlEngine getEngine() {
    return engine;
  }


}
