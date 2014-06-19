package eu.lod2.hooks.contexts.base;

import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface PostContextBase extends ContextBase {

  /**
   * Retrieves the request object as sent by the user.
   *
   * @return HttpServletRequest as sent by the user.
   */
  public HttpServletRequest getRequest();

  /**
   * Retrieves the response object which will be sent to the user.
   *
   * @return ResponseEntity which is to be sent to the user.
   */
  public ResponseEntity<Object> getResponse();

  /**
   * Retrieves the statements which have been inserted into the database for this request.
   *
   * @return Statements which have been inserted.
   */
  public Model getStatements();
}
