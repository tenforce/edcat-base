package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.format.JsonLDFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.AtContext;
import eu.lod2.hooks.contexts.PostContext;
import eu.lod2.hooks.contexts.PreContext;
import eu.lod2.hooks.handlers.dcat.AtUpdateHandler;
import eu.lod2.hooks.handlers.dcat.PostUpdateHandler;
import eu.lod2.hooks.handlers.dcat.PreUpdateHandler;
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
public class UpdateController extends DatasetController {
  // PUT /datasets/{id}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> update(HttpServletRequest request, @PathVariable String datasetId) throws Throwable {
    this.datasetId = datasetId;
    SparqlEngine engine = new SparqlEngine();
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    String datasetIdString = getId();
    URI datasetUri = catalog.generateDatasetUri(datasetIdString);
    HookManager.callHook(PreUpdateHandler.class, "handlePreUpdate", new PreContext(catalog, request, engine, datasetUri));
    Model record = catalog.updateDataset(datasetIdString);
    Model statements = buildModel(request, datasetUri);
    statements.addAll(record);
    HookManager.callHook(AtUpdateHandler.class, "handleAtUpdate", new AtContext(catalog, statements, engine));
    engine.addStatements(statements, datasetUri);
    ResponseFormatter formatter = new JsonLDFormatter(getContext());
    Object compactedJsonLD = formatter.format(statements);
    ResponseEntity<Object> response = new ResponseEntity<Object>(compactedJsonLD, getHeaders(), HttpStatus.OK);
    HookManager.callHook(PostUpdateHandler.class, "handlePostUpdate", new PostContext(catalog, response, engine, datasetUri, statements));
    engine.terminate();
    return response;
  }
}
