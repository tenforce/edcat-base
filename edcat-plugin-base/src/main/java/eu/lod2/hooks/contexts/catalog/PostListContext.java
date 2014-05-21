package eu.lod2.hooks.contexts.catalog;

import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

/**
 * The AtContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PostListHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
public class PostListContext extends Context {

  /**
   * Simple constructor for the PostListContext.
   */
  public PostListContext(ResponseEntity<Object> response, Model statements) {
    setResponse( response );
    setStatements( statements );
  }

  /** Response of this request. */
  private ResponseEntity<Object> response;

  /** Model describing the answer to the user. */
  private Model statements;

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
