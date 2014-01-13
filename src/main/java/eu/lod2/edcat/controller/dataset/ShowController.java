package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.Constants;
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
  public ResponseEntity<Object> show(HttpServletRequest request, @PathVariable String datasetId) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    URI datasetUri = catalog.generateDatasetUri(datasetId);
    HookManager.callHook(PreReadHandler.class, "handlePreRead", new PreContext(catalog, request, engine, datasetUri));
    Model statements = engine.getStatements(datasetUri);
    Object compactedJsonLD = buildJsonFromStatements(statements);
    ResponseEntity<Object> response = new ResponseEntity<Object>(compactedJsonLD, getHeaders(), HttpStatus.OK);
    HookManager.callHook(PostReadHandler.class, "handlePostRead", new PostContext(catalog, response, engine, datasetUri));
    engine.terminate();
    return response;
  }
}
