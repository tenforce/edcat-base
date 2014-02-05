package eu.lod2.hooks.contexts;

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
   */
  public PostListContext( ResponseEntity<Object> response ) {
    this.response = response;
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
