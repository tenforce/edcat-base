package eu.lod2.hooks.contexts.dataset;

import eu.lod2.hooks.contexts.base.PostListContextBase;
import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * The PostListContext is used for {@link eu.lod2.hooks.handlers.dcat.dataset.PostListHandler} and may be
 * used by other hooks which communicate similar information.
 */
public class PostListContext implements PostListContextBase {

  /**
   * Constructs a new PostListContext with all fields set.
   */
  public PostListContext( HttpServletRequest request, ResponseEntity<Object> response, Model statements ) {
    this.request = request;
    this.response = response;
    this.statements = statements;
  }

  /**
   * Request as sent by the user.
   * May be consulted to get request information.
   */
  private HttpServletRequest request;

  /**
   * Request as sent by the user.
   * May be consulted to get request information.
   */
  public HttpServletRequest getRequest() {
    return request;
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

  /**
   * Dataset statements
   */
  Model statements;

  /**
   * Returns the statements which will be created in this action.
   */
  public Model getStatements() {
    return statements;
  }

}
