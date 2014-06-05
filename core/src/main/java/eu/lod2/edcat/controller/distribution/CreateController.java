package eu.lod2.edcat.controller.distribution;

import eu.lod2.edcat.format.CompactedObjectFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.contexts.distribution.AtContext;
import eu.lod2.hooks.contexts.distribution.PostContext;
import eu.lod2.hooks.contexts.distribution.PreContext;
import eu.lod2.hooks.handlers.dcat.distribution.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.distribution.PostCreateHandler;
import eu.lod2.hooks.handlers.dcat.distribution.PreCreateHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller("DistributionCreateController")
public class CreateController extends DistributionController {

  // POST /datasets
  @RequestMapping(value = LIST_ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create( HttpServletRequest request, @PathVariable String catalogId, @PathVariable String datasetId  ) throws Throwable {
    URI datasetUri = DcatURI.datasetURI(catalogId, datasetId);
    verifyDatasetExists(datasetUri);
    URI distributionUri = DcatURI.distributionURI(datasetUri, UUID.randomUUID().toString());
    HookManager.callHook(PreCreateHandler.class, "handlePreCreate", new PreContext(request,datasetUri,distributionUri));
    Model statements = buildModel( request, distributionUri );
    statements.add(datasetUri, Sparql.namespaced("dcat","distribution"),distributionUri);
    HookManager.callHook(AtCreateHandler.class, "handleAtCreate", new AtContext(request, datasetUri, distributionUri, statements));
    Db.add(statements, datasetUri);
    ResponseFormatter formatter = new CompactedObjectFormatter( new JsonLdContext( kind ) );
    statements.remove(datasetUri,Sparql.namespaced("dcat","distribution"),distributionUri);
    Object compactedJsonLD = formatter.format( statements );
    ResponseEntity<Object> response = new ResponseEntity<Object>( compactedJsonLD, new HttpHeaders(), HttpStatus.OK );
    HookManager.callHook( PostCreateHandler.class, "handlePostCreate", new PostContext( request, response, datasetUri, distributionUri, statements ) );
    return response;
  }
}
