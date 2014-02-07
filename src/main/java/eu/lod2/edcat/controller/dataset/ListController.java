package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.*;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.hooks.contexts.PostListContext;
import eu.lod2.hooks.contexts.PreListContext;
import eu.lod2.hooks.handlers.dcat.PostListHandler;
import eu.lod2.hooks.handlers.dcat.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Lists all datasets in the application.
 */
@Controller
public class ListController extends DatasetController {

  @RequestMapping( value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> listJSON( HttpServletRequest request, @PathVariable String catalogId ) throws Throwable {
    JsonLdContext context = new JsonLdContext( JsonLdContext.Kind.Dataset );
    return list(request, new CompactedListFormatter( context ), catalogId );
  }

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/rdf+xml;charset=UTF-8")
  public ResponseEntity<Object> listXML( HttpServletRequest request, @PathVariable String catalogId ) throws Throwable {
    return list( request, new XMLRDFFormatter(), catalogId );
  }

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "text/turtle;charset=UTF-8")
  public ResponseEntity<Object> listTurtle( HttpServletRequest request, @PathVariable String catalogId ) throws Throwable {
    return list( request, new TurtleFormatter(), catalogId );
  }

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/ld+json;charset=UTF-8")
  public ResponseEntity<Object> listJSONLD( HttpServletRequest request, @PathVariable String catalogId ) throws Throwable {
    return list( request, new JsonLDFormatter(), catalogId );
  }

  /**
   * Constructs a response for the specified response formatter and calls the necessary hooks.
   *
   * @param request   Request for which we want the response.
   * @param formatter Format in which the response will be sent.
   * @return Response which can be sent to the user.
   * @throws Throwable Throws an exception if one of the hooks throws one.
   */
  public ResponseEntity<Object> list( HttpServletRequest request, ResponseFormatter formatter, String catalogId ) throws Throwable {
    HookManager.callHook( PreListHandler.class, "handlePreList", new PreListContext( request ) );
    Catalog catalog = new Catalog( catalogId );
    Model m = modelFromQueryResult( fetchDatasets( catalog.getUri(), request ) );
    Object body = formatter.format( m );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostListHandler.class, "handlePostList", new PostListContext( response ) );
    return response;
  }

  /**
   * Returns the integer which is found in the request for the "pageNumber" parameter.
   * Defaulting to 0;
   *
   * @return Supplied or default int for the pageNumber parameter.
   */
  private int getPageNumberParameter( HttpServletRequest request ) {
    return getIntParameter( request, "page", 0 );
  }

  /**
   * Returns the integer which is found in the request for the "pageSize" parameter.
   * Defaulting to 100;
   *
   * @return Supplied or default int for the pageSize parameter.
   */
  private int getPageSizeParameter( HttpServletRequest request ) {
    return getIntParameter( request, "pageSize", 100 );
  }

  /**
   * Retrieves the value of an integer parameter in the request.
   *
   * @param request      Request which contains the parameter.
   * @param name         Name of the supplied parameter.
   * @param defaultValue Value which is to be returned if the value couldn't be converted to an
   *                     integer or if no value was supplied.
   * @return integer containing the value for the get parameter with name {@code name}.
   */
  private int getIntParameter( HttpServletRequest request, String name, int defaultValue ) {
    try {
      return Integer.parseInt( request.getParameter( name ) );
    } catch ( Exception e ) {
      return defaultValue;
    }
  }

  /**
   * Fetches the information we want to list about the datasets.
   *
   * @param catalog CatalogService for which we want to list the DataSets.
   * @param request Request for which the dataset should be fetched (used for parametrization)
   * @return QueryResult containing the information we want to render out.
   * @see #modelFromQueryResult(eu.lod2.edcat.utils.QueryResult)
   */
  private QueryResult fetchDatasets( URI catalog, HttpServletRequest request ) {
    int pageSize = getPageSizeParameter( request );
    int pageNumber = getPageNumberParameter( request );
    int limit = pageSize;
    int offset = pageSize * pageNumber;

    // todo: this query selects the english title.  if neither the english title, an english description or an english themeLabel exists, this will only return the title.  That behaviour makes the subjected dataset hidden in the json output.  hence a 'best' title should be returned.  It is not possible to return all titles as that would break the semantics of the optional/limit (as it's implemented right at this time).
    return Db.query( "" +
        " @PREFIX " +
        " SELECT DISTINCT ?dataset ?description ?title ?themeLabel " +
        " WHERE {" +
        "   GRAPH $catalog {" +
        "     ?catalog dcat:dataset ?dataset." +
        "   } OPTIONAL { " +
        "     GRAPH ?dataset {" +
        "       OPTIONAL { ?dataset dct:description ?description FILTER( lang( ?description ) = \"en\") }" +
        "       OPTIONAL { ?dataset dct:title ?title FILTER( lang( ?title ) = \"en\" ) }" +
        "       OPTIONAL { " +
        "           ?dataset dcat:theme ?theme." +
        "           ?theme skos:preflabel ?themeLabel FILTER( lang( ?themeLabel ) = \"en\" )" +
        "       }" +
        "     }" +
        "   }" +
        " }" +
        (limit == 0 ? "" : " LIMIT $limit") +
        (offset == 0 ? "" : " OFFSET $offset"),
        "catalog", catalog,
        "limit", limit,
        "offset", offset );
  }

  /**
   * Constructs a Model based on the results we want to display.
   *
   * @param queryResults The answers received from the RDF store.
   * @return Model which can be converted to an output format for the end-user.
   * @see #fetchDatasets(org.openrdf.model.URI, javax.servlet.http.HttpServletRequest)
   */
  private Model modelFromQueryResult( QueryResult queryResults ) {
    Model statements = new LinkedHashModel();

    for ( Map<String, String> result : queryResults ) {
      URI dataset = new URIImpl( result.get( "dataset" ) );
      if ( result.containsKey( "description" ) )
        statements.add(
            dataset,
            Sparql.namespaced( "dct", "description" ),
            new LiteralImpl( result.get( "description" ) ),
          /* unused uri */ dataset );
      if ( result.containsKey( "title" ) )
        statements.add(
            dataset,
            Sparql.namespaced( "dct", "title" ),
            new LiteralImpl( result.get( "title" ) ),
          /* unused uri */ dataset );
      if ( result.containsKey( "theme" ) ) {
        statements.add(
            dataset,
            Sparql.namespaced( "dcat", "theme" ),
            new URIImpl( result.get( "theme" ) ),
          /* unused uri */ dataset );
        statements.add(
            dataset,
            Sparql.namespaced( "skos", "prefLabel" ),
            new LiteralImpl( result.get( "themeLabel" ) ),
          /* unused uri */ dataset );
      }
    }

    return statements;
  }
}
