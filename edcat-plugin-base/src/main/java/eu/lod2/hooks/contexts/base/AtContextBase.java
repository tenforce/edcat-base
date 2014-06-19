package eu.lod2.hooks.contexts.base;

import org.openrdf.model.Model;

import javax.servlet.http.HttpServletRequest;

public interface AtContextBase extends ContextBase {

  /**
   * Retrieves the request object as sent by the user.
   *
   * @return HttpServletRequest as sent by the user.
   */
  public HttpServletRequest getRequest();

  /**
   * Retrieves the statements which have been inserted into the database for this request.
   *
   * @return Statements which have been inserted.
   */
  public Model getStatements();
}
