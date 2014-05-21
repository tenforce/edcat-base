package eu.lod2.edcat.controller.catalog;

import eu.lod2.edcat.controller.BaseController;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import org.springframework.http.HttpHeaders;

/**
 * Superclass for controllers which operate on the catalogs.
 */
public abstract class CatalogController extends BaseController {
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
}
