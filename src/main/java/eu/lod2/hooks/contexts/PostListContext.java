package eu.lod2.hooks.contexts;

import eu.lod2.edcat.utils.SparqlEngine;
import org.springframework.http.ResponseEntity;

/**
 * The PostListContext is used for {@link eu.lod2.hooks.handlers.dcat.PostListHandler} and may be
 * used by other hooks which communicate similar information.
 */
public class PostListContext extends Context {

  /**
   * Constructs a new PostListContext with all fields set.
   *
   * @param response contains the answer which will be sent to the user.
   * @param engine   engine used for answering the request.
   */
  public PostListContext( ResponseEntity<Object> response, SparqlEngine engine ) {
    this.engine = engine;
    this.response = response;
  }

  /**
   * The SparqlEngine used for fulfilling to the request.
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

}
