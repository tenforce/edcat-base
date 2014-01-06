package eu.lod2.edcat.controller;

import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.DcatRDFHandler;
import com.github.jsonldjava.sesame.SesameTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;
import eu.lod2.edcat.handler.PreCreateHandler;
import eu.lod2.edcat.utils.Constants;
import eu.lod2.edcat.utils.SparqlEngine;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.rio.ParserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class DatasetController {
  protected static final String ROUTE = "datasets";
  protected static final String OBJECT_ROUTE = ROUTE + "/{datasetId}";
  @Autowired(required=false)
  protected List<PreCreateHandler> preCreateHandlers = Collections.emptyList();

  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    return headers;
  }


  // POST /datasets
  @RequestMapping(value = ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> create(HttpServletRequest request) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    InputStream inputStream = request.getInputStream();
    Object json = JSONUtils.fromInputStream(inputStream);
    DcatRDFHandler rdfHandler = new DcatRDFHandler();
    final SesameTripleCallback callback = new SesameTripleCallback(rdfHandler,ValueFactoryImpl.getInstance(), new ParserConfig(), null);
    JsonLdProcessor.toRDF(json, callback);
    String datasetId =  UUID.randomUUID().toString();
    String uri = buildDatasetURI(datasetId);
    URI graph = new URIImpl(uri);
    URI resource = new URIImpl(uri);
    LinkedHashModel statements = new LinkedHashModel(rdfHandler.getStatements());
    URI record = new URIImpl(buildRecordURI(datasetId));
//    for (PreCreateHandler h : preCreateHandlers) {
//      h.handle(engine,graph,resource);
//    }
    Literal now = ValueFactoryImpl.getInstance().createLiteral(new Date());
    statements.add(new StatementImpl(record, DCTERMS.ISSUED, now));
    statements.add(record,DCTERMS.MODIFIED, now);
    engine.addStatements(statements);
    inputStream.close();



    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }

  // GET /datasets/{id}
  // TODO: this is a stub
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> show(HttpServletRequest request)throws Exception {
    SparqlEngine engine = new SparqlEngine();
    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }


  // PUT /datasets/{id}
  // TODO: this is a stub
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> update(HttpServletRequest request)
          throws Exception {
    SparqlEngine engine = new SparqlEngine();
    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }

  // DELETE /datasets/{id}
  // TODO: this is a stub
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> destroy(HttpServletRequest request) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }


  private String buildDatasetURI(String datasetId) {
    StringBuilder builder = new StringBuilder();
    builder.append(Constants.getURIBase()).append(Constants.datasetURIType).append("/").append(datasetId);
    return builder.toString();
  }
  private String buildRecordURI(String datasetId) {
    StringBuilder builder = new StringBuilder();
    builder.append(Constants.getURIBase()).append("records").append("/").append(datasetId).toString();
    return builder.toString();
  }
}
