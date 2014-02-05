package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.DatasetFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.utils.CatalogService;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.contexts.AtContext;
import eu.lod2.hooks.contexts.PostContext;
import eu.lod2.hooks.contexts.PreContext;
import eu.lod2.hooks.handlers.dcat.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.PostCreateHandler;
import eu.lod2.hooks.handlers.dcat.PreCreateHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CreateController extends DatasetController {

  // POST /datasets
  @RequestMapping(value = ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create(HttpServletRequest request) throws Throwable {
    CatalogService catalogService = CatalogService.getDefaultCatalog( );
    String datasetBaseId = getId();
    URI datasetUri = catalogService.generateDatasetUri(datasetBaseId);
    HookManager.callHook(PreCreateHandler.class, "handlePreCreate", new PreContext( catalogService, request, datasetUri));
    Model record = catalogService.insertDataset(datasetBaseId);
    Model statements = buildModel(request, datasetUri);
    HookManager.callHook(AtCreateHandler.class, "handleAtCreate", new AtContext( catalogService, statements, datasetUri));
    Db.add( statements, datasetUri );
    statements.addAll(record);
    ResponseFormatter formatter = new DatasetFormatter( JsonLdContext.getContextLocation() );
    Object compactedJsonLD = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>(compactedJsonLD, getHeaders(), HttpStatus.OK);
    HookManager.callHook(PostCreateHandler.class, "handlePostCreate", new PostContext( catalogService, response, datasetUri, statements));
    return response;
  }
}
