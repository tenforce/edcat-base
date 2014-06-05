package eu.lod2.hooks.contexts.dataset;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * The PostListContext is used for {@link eu.lod2.hooks.handlers.dcat.dataset.PostListHandler} and may be
 * used by other hooks which communicate similar information.
 */
public class PostListContext extends Context {

  /**
   * Constructs a new PostListContext with all fields set.
   */
  public PostListContext( HttpServletRequest request, ResponseEntity<Object> response ) {
    this.request = request;
    this.response = response;
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

}
