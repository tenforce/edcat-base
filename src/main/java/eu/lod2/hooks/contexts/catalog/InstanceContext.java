package eu.lod2.hooks.contexts.catalog;

import eu.lod2.edcat.model.Catalog;

/**
 * Context which operates on a single Catalog instance.
 */
public abstract class InstanceContext extends Context {

  /** Catalog on which the request operated */
  private Catalog catalog;

  /**
   * Catalog on which the request operated.
   *
   * @return Catalog instance on which the request operates.
   */
  public Catalog getCatalog(){
    return catalog;
  }

  /**
   * Sets the Catalog on which this request operates.
   *
   * @param catalog Catalog
   */
  protected void setCatalog( Catalog catalog ){
    this.catalog = catalog;
  }

}
