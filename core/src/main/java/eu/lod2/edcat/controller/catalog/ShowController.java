package eu.lod2.edcat.controller.catalog;

import eu.lod2.edcat.format.CompactedObjectFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.edcat.utils.NotFoundException;
import eu.lod2.hooks.contexts.catalog.PostContext;
import eu.lod2.hooks.contexts.catalog.PreContext;
import eu.lod2.hooks.handlers.dcat.catalog.PostReadHandler;
import eu.lod2.hooks.handlers.dcat.catalog.PreReadHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BindingSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Renders basic information about the Catalog.
 */
@Controller( "CatalogShowController" )
public class ShowController extends CatalogController {

  // GET /catalogs/
  @RequestMapping( value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> createZ( HttpServletRequest request, @PathVariable String catalogId ) throws Throwable {
    Catalog catalog = new Catalog( catalogId );
    HookManager.callHook( PreReadHandler.class, "handlePreRead", new PreContext( request, catalog.getUri() ) );
    Model statements = loadStatements( catalog );
    if (statements.size() == 0 )
      throw new NotFoundException();
    ResponseFormatter formatter = new CompactedObjectFormatter( new JsonLdContext( JsonLdContext.Kind.Catalog ) );
    Object body = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostReadHandler.class, "handlePostRead", new PostContext( catalog, request, response, statements ) );
    return response;
  }

  /**
   * Loads the statements to render for the supplied catalog.
   *
   * @param catalog Catalog for which we want to receive the statements.
   * @return Model containing the statements.
   */
  public Model loadStatements( Catalog catalog ) {
    List<BindingSet> result = Db.rawQuery( "" +
        " @PREFIX" +
        " SELECT ?s ?p ?o" +
        " FROM $catalog" +
        " WHERE {" +
        "   $catalog ?p ?o. " +
        "   BIND($catalog as ?s)." +
        "   FILTER ( ?p NOT IN (dcat:dataset, dcat:record)) " +
        " }",
        "catalog", catalog.getUri() );

    Model statements = new LinkedHashModel();
    for ( BindingSet binding : result )
      statements.add(
          ( Resource ) binding.getValue( "s" ),
          ( URI ) binding.getValue( "p" ),
          binding.getValue( "o" ) );

    return statements;
  }
}
