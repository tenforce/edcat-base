package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.SparqlEngine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DeteleController extends DatasetController {
  // DELETE /datasets/{id}
  // TODO: this is a stub
  @RequestMapping(value = OBJECT_ROUTE, method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> destroy(HttpServletRequest request, @PathVariable String datasetId) throws Exception {
    SparqlEngine engine = new SparqlEngine();
    engine.terminate();
    return new ResponseEntity<Object>(null, getHeaders(), HttpStatus.OK);
  }
}
