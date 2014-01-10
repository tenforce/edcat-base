package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.OptionalHookHandler;
import eu.lod2.hooks.handlers.dcat.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.PostCreateHandler;
import eu.lod2.hooks.handlers.dcat.PreCreateHandler;
import eu.lod2.hooks.util.ActionAbortException;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.hooks.util.MultiImplementedHookException;
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
  private String datasetId;

  // POST /datasets
  @RequestMapping(value = ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create(HttpServletRequest request) throws Throwable {
    SparqlEngine engine = new SparqlEngine();
    HookManager.callHook(PreCreateHandler.class, "handlePreCreate", request, engine);
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    URI datasetUri = catalog.insertDataset(getId());
    Model statements = buildModel(request, datasetUri);
    HookManager.callHook(AtCreateHandler.class, "handleAtCreate", statements);
    engine.addStatements(statements, datasetUri);
    Object compactedJsonLD = buildJsonFromStatements(statements);
    ResponseEntity<Object> response = new ResponseEntity<Object>(compactedJsonLD, getHeaders(), HttpStatus.OK);
    HookManager.callHook(PostCreateHandler.class, "handlePostCreate", engine, response, datasetUri);
    engine.terminate();
    return response;
  }
}
