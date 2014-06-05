package eu.lod2.hooks.contexts.catalog;

import eu.lod2.edcat.model.Catalog;
import org.openrdf.model.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * The AtContext is used for:
 * <p/>
 * - {@link eu.lod2.hooks.handlers.dcat.catalog.AtCreateHandler}
 * <p/>
 * and may be used by other hooks if they communicate similar information.
 */
public class AtContext extends InstanceContext {

  /**
   * Constructs a new AtContext with all variables set.
   */
  public AtContext( Catalog catalog, HttpServletRequest request, Model statements ) {
    setCatalog( catalog );
    setRequest( request );
    setStatements( statements );
  }

}
