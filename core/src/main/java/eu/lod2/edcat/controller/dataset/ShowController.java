package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.DatasetFormatter;
import eu.lod2.edcat.format.JsonLDFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.format.TurtleFormatter;
import eu.lod2.edcat.format.XMLRDFFormatter;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.contexts.dataset.PostContext;
import eu.lod2.hooks.contexts.dataset.PreContext;
import eu.lod2.hooks.handlers.dcat.dataset.PostReadHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PreReadHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ShowController extends DatasetController {

  // GET /datasets/{datasetId}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> show( HttpServletRequest request, @PathVariable String catalogId , @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new DatasetFormatter( new JsonLdContext( kind ) );
    return show( request, formatter, catalogId );
  }

  // GET /datasets/{datasetId}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/ld+json;charset=UTF-8")
  public ResponseEntity<Object> showLD( HttpServletRequest request, @PathVariable String catalogId , @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new JsonLDFormatter();
    return show( request, formatter, catalogId );
  }

  // GET /datasets/{datasetId}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/rdf+xml;charset=UTF-8")
  public ResponseEntity<Object> showRDF( HttpServletRequest request, @PathVariable String catalogId , @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new XMLRDFFormatter();
    return show( request, formatter, catalogId );
  }

  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "text/turtle;charset=UTF-8")
  public ResponseEntity<Object> showTurtle( HttpServletRequest request, @PathVariable String catalogId , @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new TurtleFormatter();
    return show( request, formatter, catalogId );
  }

  private ResponseEntity<Object> show( HttpServletRequest request, ResponseFormatter formatter, String catalogId ) throws Throwable {
    Catalog catalog = new Catalog( catalogId );
    URI datasetUri = DcatURI.datasetURI(catalogId, datasetId);
    HookManager.callHook( PreReadHandler.class, "handlePreRead", new PreContext( catalog, request, datasetUri ) );
    Model statements = Db.getStatements( datasetUri );
    Object body = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostReadHandler.class, "handlePostRead", new PostContext( catalog, request, response, datasetUri, statements ) );
    return response;
  }
}
