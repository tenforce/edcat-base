package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.*;
import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.PostContext;
import eu.lod2.hooks.contexts.PreContext;
import eu.lod2.hooks.handlers.dcat.PostReadHandler;
import eu.lod2.hooks.handlers.dcat.PreReadHandler;
import eu.lod2.hooks.util.HookManager;
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
  public ResponseEntity<Object> show( HttpServletRequest request, @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new DatasetFormatter( JsonLdContext.getContextLocation() );
    return show(request, formatter);
  }

  // GET /datasets/{datasetId}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/ld+json;charset=UTF-8")
  public ResponseEntity<Object> showLD( HttpServletRequest request, @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter =  new JsonLDFormatter( JsonLdContext.getContextLocation() );
    return show(request, formatter);
  }

  // GET /datasets/{datasetId}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/rdf+xml;charset=UTF-8")
  public ResponseEntity<Object> showRDF( HttpServletRequest request, @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new XMLRDFFormatter();
    return show( request, formatter );
  }

  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "text/turtle;charset=UTF-8")
  public ResponseEntity<Object> showTurtle( HttpServletRequest request, @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    ResponseFormatter formatter = new TurtleFormatter();
    return show( request, formatter );
  }

  private ResponseEntity<Object> show( HttpServletRequest request, ResponseFormatter formatter ) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    Catalog catalog = Catalog.getDefaultCatalog( engine );
    URI datasetUri = catalog.generateDatasetUri( datasetId );
    HookManager.callHook( PreReadHandler.class, "handlePreRead", new PreContext( catalog, request, engine, datasetUri ) );
    Model statements = engine.getStatements( datasetUri );
    Object body = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>( body, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostReadHandler.class, "handlePostRead", new PostContext( catalog, response, engine, datasetUri, statements ) );
    engine.terminate();
    return response;
  }
}
