package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.DatasetFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.contexts.dataset.AtContext;
import eu.lod2.hooks.contexts.dataset.PostContext;
import eu.lod2.hooks.contexts.dataset.PreContext;
import eu.lod2.hooks.handlers.dcat.dataset.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PostCreateHandler;
import eu.lod2.hooks.handlers.dcat.dataset.PreCreateHandler;
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
public class CreateController extends DatasetController {

  // POST /datasets
  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create( HttpServletRequest request, @PathVariable String catalogId  ) throws Throwable {
    Catalog catalog = new Catalog( catalogId );
    verifyCatalogExists(catalog);
    String datasetBaseId = getId();
    URI datasetUri = DcatURI.datasetURI(catalogId, datasetBaseId);
    HookManager.callHook( PreCreateHandler.class, "handlePreCreate", new PreContext( catalog, request, datasetUri ) );
    Model record = catalog.createRecord(datasetBaseId);
    Model statements = buildModel( request, datasetUri );
    HookManager.callHook( AtCreateHandler.class, "handleAtCreate", new AtContext( catalog, request, statements, datasetUri ) );
    Db.add( statements, datasetUri );
    statements.addAll( record );
    ResponseFormatter formatter = new DatasetFormatter( new JsonLdContext( kind ) );
    Object compactedJsonLD = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>( compactedJsonLD, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostCreateHandler.class, "handlePostCreate", new PostContext( catalog, request, response, datasetUri, statements ) );
    return response;
  }
}
