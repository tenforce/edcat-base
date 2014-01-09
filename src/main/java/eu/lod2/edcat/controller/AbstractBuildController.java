package eu.lod2.edcat.controller;

import eu.lod2.hooks.constraints.graph.CycleException;
import eu.lod2.hooks.util.ActionAbortException;
import eu.lod2.hooks.handlers.HookHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.hooks.handlers.OptionalHookHandler;
import eu.lod2.hooks.handlers.dcat.PreCreateHandler;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.DcatJsonParser;
import eu.lod2.edcat.utils.SparqlEngine;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Date;

public abstract class AbstractBuildController {
  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    return headers;
  }



  public ResponseEntity<Object> build(HttpServletRequest request) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    preCreateHook(engine, request);
    Model statements = buildModel(request);
    atCreateHook(statements);
    engine.addStatements(statements);
    postCreateHook(engine);
    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }

  abstract public String getId();
  abstract public String getContext();

  private Model buildModel(HttpServletRequest request) throws Exception{
    InputStream in = request.getInputStream();
    String datasetId = getId();
    URI uri = buildDatasetURI(datasetId);
    Model statements = DcatJsonParser.parse(in, getContext(),uri);
    in.close();

    URI record = buildRecordURI(datasetId);
    Literal now = ValueFactoryImpl.getInstance().createLiteral(new Date());
    statements.add(new StatementImpl(record, DCTERMS.ISSUED, now));
    statements.add(record, DCTERMS.MODIFIED, now);
    return statements;
  }

  private void postCreateHook(SparqlEngine engine) {
//    for (PostCreateHandler h : postCreateHandlers) {
//     h.handlePreCreate(engine,graph,resource);
//  }
  }

  private void atCreateHook(Model statements) {
//    for (AtCreateHandler h : atCreateHandlers) {
//       h.handlePreCreate(engine,graph,resource);
//     }
  }

  private void preCreateHook(SparqlEngine engine, HttpServletRequest request) throws ActionAbortException, ClassNotFoundException, CycleException {
    for (HookHandler h : HookManager.orderedHandlers(PreCreateHandler.class)) {
      if (h instanceof  PreCreateHandler)
        ((PreCreateHandler) h).handlePreCreate(request,engine);
      else
        ((OptionalHookHandler) h).handle(PreCreateHandler.class.getCanonicalName(),request,engine);
     }

  }

  private URI buildDatasetURI(String datasetId) {
    StringBuilder builder = new StringBuilder();
    builder.append(Constants.getURIBase()).append(Constants.datasetURIType).append("/").append(datasetId);
    return new URIImpl(builder.toString());
  }

  private URI buildRecordURI(String datasetId) {
    StringBuilder builder = new StringBuilder();
    builder.append(Constants.getURIBase()).append("records").append("/").append(datasetId);
    return new URIImpl(builder.toString());
  }
}
