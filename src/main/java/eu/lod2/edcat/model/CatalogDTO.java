package eu.lod2.edcat.model;

import eu.lod2.query.Sparql;
import org.openrdf.model.URI;

import java.util.UUID;

public class CatalogDTO {

  public static CatalogDTO example() {
    CatalogDTO cat = new CatalogDTO();
    cat.setHomepage("http://www.tenforce.com/");
    cat.setIdentifier(UUID.randomUUID().toString());
    cat.setUri(Sparql.namespaced("catalogs", cat.getIdentifier()));
    return cat;
  }

  /* foaf:homepage of the catalog, the public derefencable URI to be used */
  private String homepage;

  /* internal identifier for the catalog */
  private String identifier;

  /* resource uri for the catalog */
  private URI uri;

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }
}
