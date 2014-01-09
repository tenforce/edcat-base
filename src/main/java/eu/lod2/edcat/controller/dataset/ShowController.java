package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.SparqlEngine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public class ShowController extends Datasetcontroller {

  // GET /datasets/{datasetId}
  // TODO: this is a stub
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> show(HttpServletRequest request, @RequestParam String datasetId) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    engine.terminate();
    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }
}
