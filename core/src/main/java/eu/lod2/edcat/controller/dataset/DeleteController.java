package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.hooks.contexts.dataset.PostContext;
import eu.lod2.hooks.contexts.dataset.PreContext;
import eu.lod2.hooks.handlers.dcat.dataset.PostDestroyHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PreDestroyHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
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
  public ResponseEntity<Object> destroy( HttpServletRequest request, @PathVariable String catalogId, @PathVariable String datasetId ) throws Throwable {
    Catalog catalog = new Catalog( catalogId );
    URI datasetUri = DcatURI.datasetURI(catalogId, datasetId);
    HookManager.callHook( PreDestroyHandler.class, "handlePreDestroy", new PreContext( catalog, request, datasetUri ) );
    Db.clearGraph( datasetUri );
    catalog.deleteRecord(datasetId);
    ResponseEntity<Object> response = new ResponseEntity<Object>( new HashMap(), getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostDestroyHandler.class, "handlePostDestroy", new PostContext( catalog, request, response, datasetUri, null ) );
    return response;
  }
}
