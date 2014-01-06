package eu.lod2.edcat.handler;

import eu.lod2.edcat.utils.SparqlEngine;

import javax.servlet.http.HttpServletRequest;

public interface PreCreateHandler extends HookHandler {
  public void handlePreCreate(HttpServletRequest request, SparqlEngine engine) throws ActionAbortException;
}
