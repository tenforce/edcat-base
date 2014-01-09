package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.OptionalHookHandler;
import eu.lod2.hooks.handlers.dcat.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.AtUpdateHandler;
import eu.lod2.hooks.handlers.dcat.PostCreateHandler;
import eu.lod2.hooks.handlers.dcat.PostUpdateHandler;
import eu.lod2.hooks.handlers.dcat.PreCreateHandler;
import eu.lod2.hooks.handlers.dcat.PreUpdateHandler;
import eu.lod2.hooks.util.ActionAbortException;
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
public class UpdateController extends Datasetcontroller {

  // PUT /datasets/{id}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> update(HttpServletRequest request, @PathVariable String datasetId) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    preHook(engine, request);
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    URI datasetUri = catalog.insertDataset(getId());
    Model statements = buildModel(request, datasetUri);
    atHook(statements);
    engine.addStatements(statements, datasetUri);
    Object compactedJsonLD = buildJsonFromStatements(statements);
    ResponseEntity<Object> response = new ResponseEntity<Object>(compactedJsonLD, getHeaders(), HttpStatus.OK);
    postHook(engine, response);
    engine.terminate();
    return response;
  }

  private void postHook(SparqlEngine engine, ResponseEntity<Object> response) throws ClassNotFoundException, ActionAbortException, CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PostUpdateHandler.class)) {
      if (h instanceof PreCreateHandler)
        ((PostCreateHandler) h).handlePostCreate(engine, response);
      else
        ((OptionalHookHandler) h).handle(PostUpdateHandler.class.getCanonicalName(), engine, response);
    }
  }

  private void atHook(Model statements) throws ClassNotFoundException, CycleException {
    for (HookHandler h : HookManager.orderedHandlers(AtUpdateHandler.class)) {
      if (h instanceof AtCreateHandler)
        ((AtCreateHandler) h).handleAtCreate(statements);
      else
        ((OptionalHookHandler) h).handle(AtUpdateHandler.class.getCanonicalName(), statements);
    }

  }

  private void preHook(SparqlEngine engine, HttpServletRequest request) throws ActionAbortException, ClassNotFoundException, CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PreUpdateHandler.class)) {
      if (h instanceof PreCreateHandler)
        ((PreCreateHandler) h).handlePreCreate(request, engine);
      else
        ((OptionalHookHandler) h).handle(PreUpdateHandler.class.getCanonicalName(), request, engine);
    }
  }
}
