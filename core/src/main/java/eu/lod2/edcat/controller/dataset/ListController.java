package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.*;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.hooks.contexts.dataset.PostListContext;
import eu.lod2.hooks.contexts.dataset.PreListContext;
import eu.lod2.hooks.handlers.dcat.dataset.PostListHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
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
    Catalog catalog = new Catalog(catalogId);
    verifyCatalogExists(catalog);
    Model m = fetchDatasets(catalog.getUri(), request);
    Object body = formatter.format( m );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostListHandler.class, "handlePostList", new PostListContext( request, response ) );
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
   * @param catalog catalogURI for which we want to list the DataSets.
   * @param request Request for which the dataset should be fetched (used for parametrization)
   * @return Model containing the information we want to render out.
   */
  private Model fetchDatasets( URI catalog, HttpServletRequest request ) {
    int pageSize = getPageSizeParameter( request );
    int pageNumber = getPageNumberParameter( request );
    int limit = pageSize;
    int offset = pageSize * pageNumber;

    String datasetList = buildDatasetList(catalog, limit, offset);

    if (datasetList.isEmpty())
      return new LinkedHashModel();

    return Db.construct(
        " @PREFIX " +
            "CONSTRUCT { " +
            " ?dataset a dcat:Dataset. " +
            " ?dataset dct:title ?title. " +
            " ?dataset dct:description ?desc. " +
            " ?dataset dcat:theme ?theme. " +
            " ?theme skos:prefLabel ?themeLabel. " +
            "}" +
            "WHERE {" +
            " VALUES ?dataset {$datasets}" +
            " GRAPH ?dataset { " +
            "   ?dataset dct:title ?title. " +
            "   OPTIONAL {?dataset dct:description ?desc.} " +
            "   OPTIONAL {" +
            "     ?dataset dcat:theme ?theme. " +
            "     ?theme skos:prefLabel ?themeLabel" +
            "   } " +
            " }" +
            "}",
        "catalog", catalog,
        "datasets", datasetList
    );
  }

  /**
   * Builds a value list of datasets based on the page requested
   * @param catalog
   * @param limit
   * @param offset
   * @return
   */
  private String buildDatasetList(URI catalog, int limit, int offset) {
    QueryResult r = Db.query(
        "@PREFIX " +
            "SELECT DISTINCT ?dataset " +
            "WHERE { " +
            " GRAPH $catalog {" +
            "   $catalog dcat:dataset ?dataset " +
            " }" +
            "}" +
            (limit == 0 ? "" : " LIMIT $limit") +
            (offset == 0 ? "" : " OFFSET $offset"),
        "catalog", catalog,
        "limit", limit,
        "offset", offset
    );
    StringBuilder builder = new StringBuilder();
    for (Map<String,String> map: r) {
      if (map.containsKey("dataset"))
        builder.append("<" + map.get("dataset") + ">");

    }
    return builder.toString();
  }
}
