package eu.lod2.hooks.contexts.distribution;

import eu.lod2.hooks.contexts.catalog.Context;
import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * The PostListContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.distribution.PostListHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
public class PostListContext extends Context {

  /**
   * Simple constructor for the PostListContext.
   */
  public PostListContext(HttpServletRequest request, ResponseEntity<Object> response, Model statements) {
    setRequest( request );
    setResponse( response );
    setStatements( statements );
  }

  /** Request as sent by the user. */
  private HttpServletRequest request;

  /** Response of this request. */
  private ResponseEntity<Object> response;

  /** Model describing the answer to the user. */
  private Model statements;

  /**
   * Sets the request as sent by the user in this context.
   *
   * @param request Request as sent by the user.
   */
  protected void setRequest( HttpServletRequest request){
    this.request = request;
  }

  /**
   * Sets the response which will be used to answer the request.
   *
   * @param response Response sent to the user.
   */
  protected void setResponse( ResponseEntity<Object> response ){
    this.response = response;
  }

  /**
   * Sets the statements which were used to answer this request.
   *
   * @param statements Statements used to answer the request.
   */
  protected void setStatements( Model statements ){
    this.statements = statements;
  }

  /**
   * Retrieves the request object as sent by the user.
   *
   * @return HttpServletRequest as sent by the user.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Retrieves the response which is the answer to the request.
   *
   * @return Response for the user.
   */
  public ResponseEntity<Object> getResponse(){
    return this.response;
  }

  /**
   * Retrieves the statements which were used to answer this request.
   *
   * @return Statements used to answer the request.
   */
  public Model getStatements(){
    return statements;
  }

}
