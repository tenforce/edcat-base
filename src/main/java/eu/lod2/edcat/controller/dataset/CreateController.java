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
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CreateController extends Datasetcontroller {
  private String datasetId;

  // POST /datasets
  @RequestMapping(value = ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create(HttpServletRequest request) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    preCreateHook(engine, request);
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    URI datasetUri = catalog.insertDataset(getId());
    Model statements = buildModel(request,datasetUri);
    atCreateHook(statements);
    engine.addStatements(statements,datasetUri);
    Object compactedJsonLD = buildJsonFromStatements(statements);
    ResponseEntity<Object> response = new ResponseEntity<Object>(compactedJsonLD, getHeaders(), HttpStatus.OK);
    postCreateHook(engine,response);
    engine.terminate();
    return response;
  }

  private void postCreateHook(SparqlEngine engine,ResponseEntity<Object> response) throws ClassNotFoundException, ActionAbortException,CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PostCreateHandler.class)) {
      if (h instanceof PreCreateHandler)
        ((PostCreateHandler) h).handlePostCreate(engine, response);
      else
        ((OptionalHookHandler) h).handle(PostCreateHandler.class.getCanonicalName(),engine,response);
    }
  }

  private void atCreateHook(Model statements) throws ClassNotFoundException,CycleException {
    for (HookHandler h : HookManager.orderedHandlers(AtCreateHandler.class)) {
      if (h instanceof  AtCreateHandler)
        ((AtCreateHandler) h).handleAtCreate(statements);
      else
        ((OptionalHookHandler) h).handle(AtCreateHandler.class.getCanonicalName(),statements);
    }

  }

  private void preCreateHook(SparqlEngine engine, HttpServletRequest request) throws ActionAbortException, ClassNotFoundException,CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PreCreateHandler.class)) {
      if (h instanceof  PreCreateHandler)
        ((PreCreateHandler) h).handlePreCreate(request,engine);
      else
        ((OptionalHookHandler) h).handle(PreCreateHandler.class.getCanonicalName(),request,engine);
    }
  }
}
