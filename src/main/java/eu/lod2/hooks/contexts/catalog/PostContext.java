package eu.lod2.hooks.contexts.catalog;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

/**
 * The AtContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PostCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PostReadHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
public class PostContext extends InstanceContext {

  /** Response which will be sent to the user. */
  private ResponseEntity<Object> response;

  /** Statements which have been inserted in this request */
  private Model statements;

  /**
   * Simple constructor of the PostContext.
   *
   * @param catalog Catalog on which the request operates.
   */
  public PostContext( Catalog catalog , ResponseEntity<Object> response , Model statements ){
    setCatalog( catalog );
    setResponse( response );
    setStatements( statements );
  }

  /**
   * Sets the response which is to be sent to the user in this context.
   *
   * @param response Response which is to be sent to the user.
   */
  private void setResponse( ResponseEntity<Object> response){
    this.response = response;
  }

  /**
   * Sets the statements which have been inserted in this request.
   *
   * @param statements Model containing the statements.
   */
  private void setStatements( Model statements ) {
    this.statements = statements;
  }

  /**
   * Retrieves the response object which will be sent to the user.
   *
   * @return ResponseEntity which is to be sent to the user.
   */
  private ResponseEntity<Object> getResponse(){
    return response;
  }

  /**
   * Retrieves the statements which have been inserted into the database for this request.
   *
   * @return Statements which have been inserted.
   */
  private Model getStatements(){
    return statements;
  }

}
