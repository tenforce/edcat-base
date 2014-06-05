package eu.lod2.edcat.controller.catalog;

import eu.lod2.edcat.format.*;
import eu.lod2.hooks.contexts.catalog.PostListContext;
import eu.lod2.hooks.contexts.catalog.PreListContext;
import eu.lod2.hooks.handlers.dcat.catalog.PostListHandler;
import eu.lod2.hooks.handlers.dcat.catalog.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Lists all datasets in the application.
 */
@Controller("CatalogListController")
public class ListController extends CatalogController {

  @RequestMapping( value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> listJSON( HttpServletRequest request ) throws Throwable {
    return list( request, new CompactedListFormatter( jsonLdContext ) );
  }

  @RequestMapping( value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/rdf+xml;charset=UTF-8" )
  public ResponseEntity<Object> listXML( HttpServletRequest request ) throws Throwable {
    return list( request, new XMLRDFFormatter() );
  }

  @RequestMapping( value = LIST_ROUTE, method = RequestMethod.GET, produces = "text/turtle;charset=UTF-8" )
  public ResponseEntity<Object> listTurtle( HttpServletRequest request ) throws Throwable {
    return list( request, new TurtleFormatter() );
  }

  @RequestMapping( value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/ld+json;charset=UTF-8" )
  public ResponseEntity<Object> listJSONLD( HttpServletRequest request ) throws Throwable {
    return list( request, new JsonLDFormatter() );
  }

  /**
   * Constructs a response for the specified response formatter and calls the necessary hooks.
   *
   * @param request   Request for which we want the response.
   * @param formatter Format in which the response will be sent.
   * @return Response which can be sent to the user.
   * @throws Throwable Throws an exception if one of the hooks throws one.
   */
  public ResponseEntity<Object> list( HttpServletRequest request, ResponseFormatter formatter ) throws Throwable {
    HookManager.callHook( PreListHandler.class, "handlePreList", new PreListContext( request ) );
    Model model = fetchCatalogInfo( request );
    Object body = formatter.format( model );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostListHandler.class, "handlePostList", new PostListContext( request, response , model ) );
    return response;
  }

  /**
   * Retrieves triples which describe the catalogs managed by the E-DCAT.
   *
   * @return Model describing the catalogs.
   */
  private Model fetchCatalogInfo( HttpServletRequest request ){
    return Db.construct( "" +
        " @PREFIX" +
        " CONSTRUCT { ?s ?p ?o }" +
        " WHERE {" +
        "   GRAPH @CONFIG_GRAPH {" +
        "     ?s a dcat:Catalog." +
        "   }." +
        "   GRAPH ?s {" +
        "     ?s dct:title ?o." +
        "     BIND( dct:title AS ?p )" +
        "   }" +
        " }");
  }
}