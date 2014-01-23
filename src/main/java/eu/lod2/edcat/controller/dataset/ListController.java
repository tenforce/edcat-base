package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.PostListContext;
import eu.lod2.hooks.contexts.PreListContext;
import eu.lod2.hooks.handlers.dcat.PostListHandler;
import eu.lod2.hooks.handlers.dcat.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Sparql;
import org.openrdf.model.URI;
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

  @RequestMapping( value = ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> create( HttpServletRequest request ) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    HookManager.callHook( PreListHandler.class, "handlePreList", new PreListContext( request, engine ) );
    Object datasets = fetchDatasets( engine, (URI) Sparql.getClassMapVariable( "DEFAULT_CATALOG" ) );
    ResponseEntity<Object> response = new ResponseEntity<Object>( datasets, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostListHandler.class, "handlePostList", new PostListContext( response, engine ) );
    engine.terminate();
    return response;
  }

  /**
   * Fetches the dataset in a format which may be serialized as json.
   *
   * @param engine  Connection to the RDF store for fetching information.
   * @param catalog Catalog for which we want to list the DataSets.
   * @return Object which can be serialized into json.
   */
  private Object fetchDatasets( SparqlEngine engine, URI catalog ) {
    String query = Sparql.query( "" +
      " @PREFIX " +
      " SELECT ?dataset ?description ?title ?mainTheme" +
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
      "         ?theme skos:preflabel ?mainTheme FILTER( lang( ?mainTheme ) = \"en\" ) }" +
      "     }" +
      "   }" +
      " } ",
      "catalog", catalog );

    QueryResult queryResults = engine.sparqlSelect( query );

    Map<String, Object> catalogDescription = new HashMap<String, Object>();
    catalogDescription.put( "id", catalog.stringValue() );
    catalogDescription.put( "datasets", queryResults );

    return catalogDescription;
  }
}
