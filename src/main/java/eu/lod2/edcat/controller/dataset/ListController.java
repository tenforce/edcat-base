package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.DcatJsonFormatter;
import eu.lod2.edcat.format.JsonLDFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.format.TurtleFormatter;
import eu.lod2.edcat.format.XMLRDFFormatter;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.PostListContext;
import eu.lod2.hooks.contexts.PreListContext;
import eu.lod2.hooks.handlers.dcat.PostListHandler;
import eu.lod2.hooks.handlers.dcat.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Lists all datasets in the application.
 */
@Controller
public class ListController extends DatasetController {

  @RequestMapping( value = ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> create( HttpServletRequest request ) throws Throwable {
    return create( request, new DcatJsonFormatter( getContext() ) );
  }

  @RequestMapping( value = ROUTE, method = RequestMethod.GET, produces = "application/rdf+xml;charset=UTF-8" )
  public ResponseEntity<Object> createXML( HttpServletRequest request ) throws Throwable {
    return create( request, new XMLRDFFormatter() );
  }

  @RequestMapping( value = ROUTE, method = RequestMethod.GET, produces = "text/turtle;charset=UTF-8" )
  public ResponseEntity<Object> createTurtle( HttpServletRequest request ) throws Throwable {
    return create( request, new TurtleFormatter() );
  }

  @RequestMapping( value = ROUTE, method = RequestMethod.GET, produces = "application/ld+json;charset=UTF-8" )
  public ResponseEntity<Object> createJSONLD( HttpServletRequest request ) throws Throwable {
    return create( request, new JsonLDFormatter( getContext() ) );
  }

  /**
   * Constructs a response for the specified response formatter and calls the necessary hooks.
   *
   * @param request   Request for which we want the response.
   * @param formatter Format in which the response will be sent.
   * @return Response which can be sent to the user.
   * @throws Throwable Throws an exception if one of the hooks throws one.
   */
  public ResponseEntity<Object> create( HttpServletRequest request, ResponseFormatter formatter ) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    HookManager.callHook( PreListHandler.class, "handlePreList", new PreListContext( request, engine ) );
    Model m = modelFromQueryResult( fetchDatasets( engine, (URI) Sparql.getClassMapVariable( "DEFAULT_CATALOG" ) ) );
    Object body = formatter.format( m );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostListHandler.class, "handlePostList", new PostListContext( response, engine ) );
    engine.terminate();
    return response;
  }

  /**
   * Fetches the information we want to list about the datasets.
   *
   * @param engine  Connection to the RDF store for fetching information.
   * @param catalog Catalog for which we want to list the DataSets.
   * @return QueryResult containing the information we want to render out.
   * @see #modelFromQueryResult(eu.lod2.edcat.utils.QueryResult)
   */
  private QueryResult fetchDatasets( SparqlEngine engine, URI catalog ) {
    String query = Sparql.query( "" +
      " @PREFIX " +
      " SELECT ?dataset ?description ?title ?theme ?themeLabel" +
      " WHERE {" +
      "   GRAPH $catalog {" +
      "     ?catalog dcat:dataset ?dataset." +
      "   }." +
      "   GRAPH ?dataset {" +
      "     OPTIONAL {" +
      "       { ?dataset dct:description ?description FILTER( lang( ?description ) = \"en\") }" +
      "       UNION " +
      "       { ?dataset dct:title ?title FILTER( lang( ?title ) = \"en\" ) }" +
      "       UNION " +
      "       { ?dataset dcat:theme ?theme." +
      "         ?theme skos:preflabel ?themeLabel FILTER( lang( ?themeLabel ) = \"en\" ) }" +
      "     }" +
      "   }" +
      " } ",
      "catalog", catalog );

    return engine.sparqlSelect( query );
  }

  /**
   * Constructs a Model based on the results we want to display.
   *
   * @param queryResults The answers received from the RDF store.
   * @return Model which can be converted to an output format for the end-user.
   * @see #fetchDatasets(eu.lod2.edcat.utils.SparqlEngine, org.openrdf.model.URI)
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
