package eu.lod2.edcat.model;

import eu.lod2.hooks.contexts.CatalogInstallationContext;
import eu.lod2.hooks.contexts.InstallationContext;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;
import eu.lod2.hooks.handlers.dcat.CatalogInstallationHandler;
import eu.lod2.hooks.handlers.dcat.InstallationHandler;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import eu.lod2.query.Sparql;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
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


  // --- PUBLIC INTERFACE

  /**
   * Sets up a new DCAT installation.
   */
  public static void install() throws Throwable {
    setupDatabase();
    setupCatalog( new Catalog() );
  }

  /**
   * Constructs a new catalog and installs it in the database.
   *
   * @param catalog Catalog representing the catalog.
   */
  public static void setupCatalog( Catalog catalog ) throws Throwable {
    Model statements =  new LinkedHashModel();
    Resource configGraph = new URIImpl(Sparql.getClassMapVariable("CONFIG_GRAPH").toString());
    statements.add(catalog.getUri(), RDF.TYPE, Sparql.namespaced("dcat","Catalog") );
    Db.add(statements,configGraph);

    try {
      HookManager.callHook( CatalogInstallationHandler.class,
          "handleCatalogInstall",
          new CatalogInstallationContext( catalog.getUri() ) );
    } catch ( ActionAbortException aae ) {
      LogFactory.getLog( "setupCatalog" ).error( aae.getMessage() );
    }
  }

  // --- IMPLEMENTATION

  /**
   * Injects the basic triples into the database, needed to get the base DCAT installation running.
   */
  private static void setupDatabase( ) throws Throwable {
    storeBaseDatabase( );

    try {
      HookManager.callHook( InstallationHandler.class, "handleInstall", new InstallationContext( ) );
    } catch ( ActionAbortException aae ) {
      LogFactory.getLog( "Installation" ).error( aae.getMessage() );
    }
  }

  /**
   * Imports the base Turtle graph into the database so the required structures are available.
   */
  private static void storeBaseDatabase( ) {
    // fetch file
    InputStream configFileInput = null;
    try {
      configFileInput = DEFAULT_CONFIG_GRAPH_URL.openStream();
      // parse file
      Model validationRules = Rio.parse(
          configFileInput,
          (( URI ) Sparql.getClassMapVariable( "CONFIG_GRAPH" )).stringValue(),
          RDFFormat.TURTLE );
      // add statements
      Db.add( validationRules, ( URI ) Sparql.getClassMapVariable( "CONFIG_GRAPH" ) );
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
}
