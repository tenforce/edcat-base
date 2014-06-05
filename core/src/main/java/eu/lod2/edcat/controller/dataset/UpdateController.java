package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.DatasetFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.contexts.dataset.AtContext;
import eu.lod2.hooks.contexts.dataset.PostContext;
import eu.lod2.hooks.contexts.dataset.PreContext;
import eu.lod2.hooks.handlers.dcat.dataset.AtUpdateHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PostUpdateHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PreUpdateHandler;
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
public class UpdateController extends DatasetController {
  // PUT /datasets/{id}
  @RequestMapping( value = OBJECT_ROUTE, method = RequestMethod.PUT, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> update( HttpServletRequest request, @PathVariable String catalogId, @PathVariable String datasetId ) throws Throwable {
    this.datasetId = datasetId;
    Catalog catalog = new Catalog( catalogId );
    URI datasetUri = DcatURI.datasetURI(catalogId, getId());
    HookManager.callHook( PreUpdateHandler.class, "handlePreUpdate", new PreContext( catalog, request, datasetUri ) );
    Model record = catalog.updateRecord(getId());
    Model statements = buildModel( request, datasetUri );
    statements.addAll( record );
    HookManager.callHook( AtUpdateHandler.class, "handleAtUpdate", new AtContext( catalog, request, statements, datasetUri ) );
    Db.clearGraph( datasetUri );
    Db.add( statements, datasetUri );
    ResponseFormatter formatter = new DatasetFormatter( new JsonLdContext( kind ) );
    Object compactedJsonLD = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>( compactedJsonLD, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostUpdateHandler.class, "handlePostUpdate", new PostContext( catalog, request, response, datasetUri, statements ) );
    return response;
  }
}