package eu.lod2.edcat.controller.catalog;


import com.github.jsonldjava.core.JsonLdError;
import eu.lod2.edcat.format.CompactedObjectFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.model.Catalog;
import eu.lod2.edcat.utils.JsonLD;
import eu.lod2.hooks.contexts.catalog.AtContext;
import eu.lod2.hooks.contexts.catalog.PostContext;
import eu.lod2.hooks.contexts.catalog.PreContext;
import eu.lod2.hooks.handlers.dcat.catalog.AtCreateHandler;
import eu.lod2.hooks.handlers.dcat.catalog.PostCreateHandler;
import eu.lod2.hooks.handlers.dcat.catalog.PreCreateHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@Controller( "CatalogCreateController" )
public class CreateController extends CatalogController {

  // POST /datasets
  @RequestMapping( value = LIST_ROUTE, method = RequestMethod.POST, produces = "application/json;charset=UTF-8" )
  public ResponseEntity<Object> createZ( HttpServletRequest request ) throws Throwable {
    Catalog catalog = new Catalog();
    HookManager.callHook( PreCreateHandler.class, "handlePreCreate", new PreContext( request, catalog.getUri() ) );
    constructRequestedCatalog( request, catalog );
    Model statements = catalog.getStatements();
    HookManager.callHook( AtCreateHandler.class, "handleAtCreate", new AtContext( catalog, request, statements ) );
    Db.add( statements );
    ResponseFormatter formatter = new CompactedObjectFormatter( jsonLdContext );
    Object compactedJsonLD = formatter.format( statements.filter(null,null,null,catalog.getUri()) );
    ResponseEntity<Object> response = new ResponseEntity<Object>( compactedJsonLD, getHeaders(), HttpStatus.OK );
    HookManager.callHook( PostCreateHandler.class, "handlePostCreate", new PostContext( catalog, request, response, statements ) );
    return response;
  }

  private void constructRequestedCatalog( HttpServletRequest request, Catalog catalog ) throws IOException, JsonLdError {
    // get base documents
    InputStream in = request.getInputStream();

    // add JsonLD content
    JsonLD json = JsonLD.parse( in );
    catalog.add( json );
    in.close();
  }
}