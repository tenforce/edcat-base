package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.handlers.dcat.PostDestroyHandler;
import eu.lod2.hooks.handlers.dcat.PreDestroyHandler;
import eu.lod2.hooks.util.HookManager;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class DeleteController extends DatasetController {
  // DELETE /datasets/{id}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> destroy(HttpServletRequest request, @PathVariable String datasetId) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    URI datasetUri = catalog.generateDatasetUri(datasetId);
    HookManager.callHook(PreDestroyHandler.class, "handlePreDestroy", catalog, request, engine, datasetUri);
    engine.clearGraph(catalog.generateDatasetUri(datasetId).stringValue());
    catalog.removeDataset(datasetId);
    ResponseEntity<Object> response = new ResponseEntity<Object>(new HashMap(), getHeaders(), HttpStatus.OK);
    HookManager.callHook(PostDestroyHandler.class, "handlePostDestroy", catalog, request, engine, datasetUri);
    engine.terminate();
    return response;
  }
}
