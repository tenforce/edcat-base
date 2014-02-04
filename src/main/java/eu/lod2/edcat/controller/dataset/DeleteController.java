package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.CatalogService;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.PostContext;
import eu.lod2.hooks.contexts.PreContext;
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
  public ResponseEntity<Object> destroy( HttpServletRequest request, @PathVariable String datasetId ) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    CatalogService catalogService = CatalogService.getDefaultCatalog( engine );
    URI datasetUri = catalogService.generateDatasetUri( datasetId );
    HookManager.callHook( PreDestroyHandler.class, "handlePreDestroy", new PreContext( catalogService, request, engine, datasetUri ) );
    engine.clearGraph( catalogService.generateDatasetUri( datasetId ) );
    catalogService.removeDataset( datasetId );
    ResponseEntity<Object> response = new ResponseEntity<Object>( new HashMap(), getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostDestroyHandler.class, "handlePostDestroy", new PostContext( catalogService, response, engine, datasetUri, null ) );
    engine.terminate();
    return response;
  }
}
