package eu.lod2.edcat.controller.distribution;

import eu.lod2.edcat.format.*;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.hooks.contexts.distribution.PostListContext;
import eu.lod2.hooks.contexts.distribution.PreListContext;
import eu.lod2.hooks.handlers.dcat.distribution.PostListHandler;
import eu.lod2.hooks.handlers.dcat.distribution.PreListHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Lists all distributions in the dataset.
 */
@Controller("DistributionListController")
public class ListController extends DistributionController {

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> listJSON(HttpServletRequest request, @PathVariable String catalogId, @PathVariable String datasetId) throws Throwable {
    return list(request,catalogId, datasetId, new CompactedListFormatter(jsonLdContext));
  }

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/rdf+xml;charset=UTF-8")
  public ResponseEntity<Object> listXML(HttpServletRequest request, @PathVariable String catalogId, @PathVariable String datasetId) throws Throwable {
    return list(request,catalogId, datasetId, new XMLRDFFormatter());
  }

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "text/turtle;charset=UTF-8")
  public ResponseEntity<Object> listTurtle(HttpServletRequest request, @PathVariable String catalogId, @PathVariable String datasetId) throws Throwable {
    return list(request,catalogId, datasetId, new TurtleFormatter());
  }

  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.GET, produces = "application/ld+json;charset=UTF-8")
  public ResponseEntity<Object> listJSONLD(HttpServletRequest request, @PathVariable("catalogId") String catalogId, @PathVariable String datasetId) throws Throwable {
    return list(request,catalogId, datasetId, new JsonLDFormatter());
  }

  /**
   * Constructs a response for the specified response formatter and calls the necessary hooks.
   *
   * @param request   Request for which we want the response.
   * @param catalogId UUID of the catalog
   * @param datasetId UUID of the dataset
   * @param formatter Format in which the response will be sent.
   * @return Response which can be sent to the user.
   * @throws Throwable Throws an exception if one of the hooks throws one.
   */
  public ResponseEntity<Object> list(HttpServletRequest request, String catalogId, String datasetId, ResponseFormatter formatter) throws Throwable {
    HookManager.callHook(PreListHandler.class, "handlePreList", new PreListContext(request));
    URI datasetUri = DcatURI.datasetURI(catalogId, datasetId);
    verifyDatasetExists(datasetUri);
    URI catalogUri = DcatURI.catalogUri(catalogId);
    Model model = getDistributions(catalogUri, datasetUri);
    Object body = formatter.format(model);
    ResponseEntity<Object> response = new ResponseEntity<Object>(body, new HttpHeaders(), HttpStatus.OK);
    HookManager.callHook(PostListHandler.class, "handlePostList", new PostListContext(request, response, model));
    return response;
  }

  /**
   * Retrieves triples which describe the catalogs managed by the E-DCAT.
   *
   * @return Model describing the catalogs.
   */
  private Model getDistributions(URI catalogUri, URI datasetUri) {
    return Db.construct("" +
        " @PREFIX" +
        " CONSTRUCT { ?s ?p ?o }" +
        " WHERE {" +
        "   GRAPH ?catalog {" +
        "     $catalog dcat:dataset $dataset. " +
        "   } " +
        "   GRAPH $dataset {" +
        "     $dataset dcat:distribution ?s. " +
        "     ?s ?p ?o" +
        "   }" +
        " }",
        "dataset", datasetUri,
        "catalog", catalogUri);
  }
}
