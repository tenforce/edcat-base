package eu.lod2.edcat.init;

import eu.lod2.edcat.utils.SparqlEngine;
import eu.lod2.hooks.contexts.CatalogInstallationContext;
import eu.lod2.hooks.contexts.InstallationContext;
import eu.lod2.hooks.handlers.dcat.CatalogInstallationHandler;
import eu.lod2.hooks.handlers.dcat.InstallationHandler;
import eu.lod2.hooks.util.ActionAbortException;
import eu.lod2.hooks.util.HookManager;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This file helps in the Installation/Bootstrapping of a DCAT.  Consider it to be the entry point
 * of a new DCAT installation.
 */
public class Installation {


  // --- SETTINGS

  /** Resource path to the default config graph. */
  private static String DEFAULT_CONFIG_GRAPH_PATH = "/eu/lod2/edcat/init/defaultConfigGraph.ttl";

  /** URI which contains the default config graph. */
  private static URL DEFAULT_CONFIG_GRAPH_URL =
      Installation.class.getResource( DEFAULT_CONFIG_GRAPH_PATH );

  /** Base URI for the DCAT store. */
  private static String BASE_URI = "http://lod2.tenforce.com/edcat/";

  /** Base URI for the config graph. */
  private static String CONFIG_BASE_URI = BASE_URI + "example/config";

  /** URI which contains the location of the graph containing the Catalog configuration */
  private static URI CONFIG_GRAPH_URI = new URIImpl( CONFIG_BASE_URI );

  /**
   * Namespace in which the catalogs are placed.
   * <p/>
   * A catalog could for instance be in http://lod2.tenforce.com/edcat/catalogs/Example.
   */
  private static String CATALOGS_NAMESPACE = BASE_URI + "catalogs/";

  /** Turtle 'a' statement in URI format */
  private static URI RDF_A = new URIImpl( "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" );

  /** The class which a catalog has in the RDF store. */
  private static URI RDF_CATALOG_TYPE = new URIImpl( BASE_URI + "terms/config/ValidationRule" );


  // --- PUBLIC INTERFACE

  /**
   * Sets up a new DCAT installation.
   */
  public static void install() throws Throwable {
    SparqlEngine engine = new SparqlEngine();

    setupDatabase( engine );
    setupCatalog(engine, "example");
  }

  /**
   * Constructs a new catalog and installs it in the database.
   *
   * @param name Name of the new catalog.  The URI and graph for the catalog will be based on the
   *             supplied name.
   */
  public static void setupCatalog( SparqlEngine engine, String name ) throws Throwable {
    URI catalogUri = new URIImpl( CATALOGS_NAMESPACE + name );

    storeCatalog( engine, catalogUri );

    try {
      HookManager.callHook( CatalogInstallationHandler.class,
          "handleCatalogInstall",
          new CatalogInstallationContext( engine, catalogUri ) );
    } catch ( ActionAbortException aae ) {
      LogFactory.getLog( "setupCatalog" ).error( aae.getMessage() );
    }
  }

  // --- IMPLEMENTATION

  /**
   * Injects the basic triples into the database, needed to get the base DCAT installation running.
   */
  private static void setupDatabase( SparqlEngine engine ) throws Throwable {
    storeBaseDatabase( engine );

    try {
      HookManager.callHook( InstallationHandler.class, "handleInstall", new InstallationContext( engine ) );
    } catch ( ActionAbortException aae ) {
      LogFactory.getLog( "Installation" ).error( aae.getMessage() );
    }
  }

  /**
   * Imports the base Turtle graph into the database so the required structures are available.
   *
   * @param engine Engine with a connection to the RDF store in which we add the graph.
   */
  private static void storeBaseDatabase( SparqlEngine engine ) {
    // fetch file
    InputStream configFileInput = null;
    try {
      configFileInput = DEFAULT_CONFIG_GRAPH_URL.openStream();
      // parse file
      Model validationRules = Rio.parse( configFileInput, CONFIG_BASE_URI, RDFFormat.TURTLE );
      // add statements
      engine.addStatements( validationRules, CONFIG_GRAPH_URI );
      configFileInput.close();
      // catch any errors
    } catch ( IOException e ) {
      LoggerFactory
          .getLogger( "Installation" )
          .error( "IO exception when reading " + DEFAULT_CONFIG_GRAPH_PATH );
      if ( configFileInput != null )
        try {
          configFileInput.close();
        } catch ( IOException e1 ) {
          LoggerFactory
              .getLogger( "Installation" )
              .error( "And couldn't clean up file descriptor for " + DEFAULT_CONFIG_GRAPH_PATH );
        }
    } catch ( RDFParseException e ) {
      LoggerFactory
          .getLogger( "Installation" )
          .error( "Failed to parse " + DEFAULT_CONFIG_GRAPH_PATH );
    }
  }

  /**
   * Sets up the config graph so it knows about the catalog.
   *
   * @param engine     Engine with a connection to the RDF store in which we have to add the
   *                   identification of the catalog.
   * @param catalogUri URI of the graph and id of the Catalog which we want to create.
   */
  private static void storeCatalog( SparqlEngine engine, URI catalogUri ) {
    Model m = new LinkedHashModel();
    m.add( catalogUri, RDF_A, RDF_CATALOG_TYPE );
    engine.addStatements( m, CONFIG_GRAPH_URI );
  }
}
