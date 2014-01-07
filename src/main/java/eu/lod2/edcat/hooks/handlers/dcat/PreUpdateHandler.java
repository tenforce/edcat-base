package eu.lod2.edcat.hooks.handlers.dcat;

import eu.lod2.edcat.hooks.handlers.HookHandler;
import eu.lod2.edcat.hooks.util.ActionAbortException;
import eu.lod2.edcat.utils.SparqlEngine;

import javax.servlet.http.HttpServletRequest;

public interface PreUpdateHandler extends HookHandler {
  public void handlePreUpdate(HttpServletRequest request, SparqlEngine engine) throws ActionAbortException;

}
