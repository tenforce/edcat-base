package eu.lod2.hooks.contexts.catalog;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;

/**
 * The AtContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.AtCreateHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
public class AtContext extends InstanceContext {

  /** Contains the model which defines the new triples created in this AtContext. */
  private Model statements;

  /**
   * Simple constructor of the AtContext.
   *
   * @param catalog Catalog on which the request operates.
   */
  public AtContext( Catalog catalog, Model statements ) {
    setCatalog( catalog );
    setStatements( statements );
  }

  /**
   * Sets the statements of the model.
   *
   * @param statements The statements available in this AtContext.
   */
  protected void setStatements( Model statements ) {
    this.statements = statements;
  }

  /**
   * Retrieves the statements which will be created in this action.
   *
   * @return Model of this AtContext.
   */
  public Model getStatements() {
    return statements;
  }

}
