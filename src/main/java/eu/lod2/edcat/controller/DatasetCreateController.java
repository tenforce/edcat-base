package eu.lod2.edcat.controller;

import eu.lod2.edcat.utils.SparqlEngine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class DatasetCreateController extends AbstractBuildController {
  protected static final String ROUTE = "datasets";
  protected static final String OBJECT_ROUTE = ROUTE + "/{datasetId}";

  // POST /datasets
  @RequestMapping(value = ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create(HttpServletRequest request) throws Exception {
    return build(request);
  }

  @Override
  public String getId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String getContext() {
    return "dataset"; //TODO: use valid uri
  }

  // TODO: move to seperate controllers

  // GET /datasets/{id}
  // TODO: this is a stub
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> show(HttpServletRequest request) throws Exception {
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


}
