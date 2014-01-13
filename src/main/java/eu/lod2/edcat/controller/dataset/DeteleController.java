package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.Catalog;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.handlers.OptionalHookHandler;
import eu.lod2.hooks.handlers.dcat.PostReadHandler;
import eu.lod2.hooks.handlers.dcat.PreCreateHandler;
import eu.lod2.hooks.handlers.dcat.PreReadHandler;
import eu.lod2.hooks.util.ActionAbortException;
import eu.lod2.hooks.util.HookManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class DeteleController extends DatasetController {
  // DELETE /datasets/{id}
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> destroy(HttpServletRequest request, @PathVariable String datasetId) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    Catalog catalog = new Catalog(engine, Constants.getURIBase());
    preHook(engine, request);
    engine.clearGraph(catalog.generateDatasetUri(datasetId).stringValue());
    catalog.removeDataset(datasetId);
    ResponseEntity<Object> response = new ResponseEntity<Object>(new HashMap(), getHeaders(), HttpStatus.OK);
    postHook(engine, response);
    engine.terminate();
    return response;
  }

  private void postHook(SparqlEngine engine, ResponseEntity<Object> response) throws ClassNotFoundException, ActionAbortException, CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PostReadHandler.class)) {
      if (h instanceof PreCreateHandler)
        ((PostReadHandler) h).handlePostRead(engine, response);
      else
        ((OptionalHookHandler) h).handle(PostReadHandler.class.getCanonicalName(), engine, response);
    }
  }

  private void preHook(SparqlEngine engine, HttpServletRequest request) throws ActionAbortException, ClassNotFoundException, CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PreReadHandler.class)) {
      if (h instanceof PreCreateHandler)
        ((PreReadHandler) h).handlePreRead(request, engine);
      else
        ((OptionalHookHandler) h).handle(PreReadHandler.class.getCanonicalName(), request, engine);
    }
  }
}
