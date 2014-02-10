package eu.lod2.edcat.controller.catalog;

import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Superclass for controllers which operate on the catalogs.
 */
public abstract class CatalogController {

  /** Logging aid. */
  Logger logger = LoggerFactory.getLogger( this.getClass() );

  /** Kind of object used by the JsonLdContext. */
  static JsonLdContext.Kind kind = JsonLdContext.Kind.Catalog;

  /** JsonLdContext on which the controllers operate. */
  JsonLdContext jsonLdContext = new JsonLdContext( kind );

  // --- routing

  /** catalogs route */
  protected static final String LIST_ROUTE = DcatURI.CATALOG_LIST_PATH;

  /** show catalog route */
  protected static final String OBJECT_ROUTE = DcatURI.CATALOG_OBJECT_PATH;

  //* Returns default headers for the application. These headers should always be present
  protected HttpHeaders getHeaders() throws Exception {
    return new HttpHeaders();
  }

  @ExceptionHandler( ActionAbortException.class )
  public ResponseEntity handleError( HttpServletRequest req, ActionAbortException exception ) {
    logger.error( "Request: " + req.getRequestURL() + " raised " + exception );

    return new ResponseEntity<Object>( exception, exception.getStatus() );
  }

}
