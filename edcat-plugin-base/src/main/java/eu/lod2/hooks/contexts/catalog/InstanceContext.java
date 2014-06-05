package eu.lod2.hooks.contexts.catalog;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * Context which operates on a single Catalog instance.
 */
public abstract class InstanceContext extends Context {

  /** Catalog on which the request operated */
  private Catalog catalog;

  /** Request as sent by the user. */
  private HttpServletRequest request;

  /** Statements which have been inserted in this request */
  private Model statements;

  /**
   * Catalog on which the request operated.
   *
   * @return Catalog instance on which the request operates.
   */
  public Catalog getCatalog(){
    return catalog;
  }

  /**
   * Sets the Catalog on which this request operates.
   *
   * @param catalog Catalog
   */
  protected void setCatalog( Catalog catalog ){
    this.catalog = catalog;
  }

  /**
   * Retrieves the statements which have been inserted into the database for this request.
   *
   * @return Statements which have been inserted.
   */
  public Model getStatements(){
    return statements;
  }

  /**
   * Sets the statements which have been inserted in this request.
   *
   * @param statements Model containing the statements.
   */
  protected void setStatements( Model statements ) {
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
   * Sets the request as sent by the user in this context.
   *
   * @param request Request as sent by the user.
   */
  protected void setRequest( HttpServletRequest request){
    this.request = request;
  }

}
