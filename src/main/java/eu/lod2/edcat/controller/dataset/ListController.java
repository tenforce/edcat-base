package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.PostListContext;
import eu.lod2.hooks.contexts.PreListContext;
import eu.lod2.hooks.handlers.dcat.PostListHandler;
import eu.lod2.hooks.handlers.dcat.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Lists all datasets in the application.
 */
@Controller
public class ListController extends DatasetController {

  final public static String SPARQL_PREFIXES = " " +
    "PREFIX : <http://lod2.tenforce.com/edcat/example/config/> \n" +
    "PREFIX dct: <http://purl.org/dc/terms/> \n" +
    "PREFIX dcat: <http://www.w3.org/ns/dcat#> \n" +
    "PREFIX edcat: <http://lod2.tenforce.com/edcat/terms/> \n" +
    "PREFIX cterms: <http://lod2.tenforce.com/edcat/terms/config/> \n" +
    "PREFIX catalogs: <http://lod2.tenforce.com/edcat/catalogs/> \n";

  final private static String DEFAULT_CATALOG = "http://lod2.tenforce.com/edcat/catalogs/example";

  @RequestMapping( value = ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> create( HttpServletRequest request ) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    HookManager.callHook( PreListHandler.class, "handlePreList", new PreListContext( request, engine ) );
    Object datasets = fetchDatasets( engine, new URIImpl( DEFAULT_CATALOG ) );
    ResponseEntity<Object> response = new ResponseEntity<Object>( datasets, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostListHandler.class, "handlePostList", new PostListContext( response, engine ) );
    engine.terminate();
    return response;
  }

  /**
   * Fetches the dataset in a format which may be serialized as json.
   *
   * @param engine Connection to the RDF store for fetching information.
   * @param catalog Catalog for which we want to list the DataSets.
   * @return Object which can be serialized into json.
   */
  private Object fetchDatasets( SparqlEngine engine, URI catalog ) {
    String query = "" +
      SPARQL_PREFIXES +
      "SELECT ?dataset ?description ?title \n" +
      "WHERE { \n" +
      "  GRAPH <" + catalog.stringValue() + "> { \n" +
      "      <" + catalog.stringValue() + "> dcat:dataset ?dataset.\n" +
      "  }.\n" +
      "  GRAPH ?dataset { \n" +
      "    OPTIONAL { { ?dataset dct:description ?description FILTER( lang(?description) = \"en\" ) }\n" +
      "               UNION\n" +
      "               { ?dataset dct:title ?title FILTER( lang(?title) = \"en\" ) } }\n" +
      "  }\n" +
      "}";

    QueryResult queryResults = engine.sparqlSelect( query );

    Map<String, Object> catalogDescription = new HashMap<String, Object>();
    catalogDescription.put( "id", catalog.stringValue() );
    catalogDescription.put( "datasets", queryResults );

    return catalogDescription;
  }
}
