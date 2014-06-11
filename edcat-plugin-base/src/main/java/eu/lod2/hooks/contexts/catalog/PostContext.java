package eu.lod2.hooks.contexts.catalog;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * The PostContext is used for:
 *
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PostCreateHandler}
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.PostReadHandler}
 *
 * and may be used by other hooks if they communicate similar information.
 */
public class PostContext extends InstanceContext {

  /** Response which will be sent to the user. */
  private ResponseEntity<Object> response;

  /**
   * Constructs a new PostContext with all variables set.
   */
  public PostContext( Catalog catalog , HttpServletRequest request, ResponseEntity<Object> response , Model statements ){
    setCatalog( catalog );
    setRequest( request );
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
   * Retrieves the response object which will be sent to the user.
   *
   * @return ResponseEntity which is to be sent to the user.
   */
  public ResponseEntity<Object> getResponse(){
    return response;
  }

}
