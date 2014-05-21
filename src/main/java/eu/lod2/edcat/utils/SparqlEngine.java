package eu.lod2.edcat.utils;

import eu.lod2.query.Sparql;
import info.aduna.iteration.Iterations;
import org.openrdf.model.*;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Class to take care of interactions with the sparql endpoint.
 * FIXME Currently does not reuse connections across sessions. We may have to look into this if
 * application is not fast enough -> how to get session independent? Is this done automatically?
 * Check with dominique
 * NOTE: (niels) I believe we can use something like http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html
 * if necessary
 */
public class SparqlEngine {
  private static final Logger log = LoggerFactory.getLogger( SparqlEngine.class );

  protected Repository repository;
  protected RepositoryConnection connection;


  public SparqlEngine() {
    this.readConfiguration();
  }

  public SparqlEngine( Repository repos ) {
    try {
      this.repository = repos;
      if ( !repos.isInitialized() ) {
        repos.initialize();
      }
      this.connection = this.repository.getConnection();
    } catch ( Exception e ) {
      log.error( "Could not establish connection to repository, error message: {}", e );
    }

  }

  public void readConfiguration() {
    Properties properties = new Properties();
    try {
      if (System.getProperty("ext.properties.dir") == null) throw new IllegalArgumentException("System property 'ext.properties.dir' is not set.");
      properties.load( new FileInputStream(new File(System.getProperty("ext.properties.dir"), "sparql.properties") ) );
      String storeType = properties.getProperty("storeType");

      switch (StoreType.valueOf(storeType)) {
        case memory:
          initMemoryStore();
          break;
        case virtuoso:
          initVirtuosoStore(properties);
          break;
        case sesame_remote:
          initRemoteSesameStore(properties);
          break;
        default:
          throw new IllegalArgumentException("invalid store type specified in sparql.properties");
      }
      this.repository.initialize();
      this.connection = this.repository.getConnection();
    } catch ( Exception e ) {
      log.error( "Could not establish connection to repository, error message: {}", e );
      throw new RuntimeException(e);
    }
  }

  private void initMemoryStore() throws Exception {
    this.repository = new SailRepository( new MemoryStore() );
  }

  private void initRemoteSesameStore(Properties properties) throws Exception {
    RepositoryManager repositoryManager =   new RemoteRepositoryManager(properties.getProperty("sesame_url"));
    repositoryManager.initialize();
    this.repository = repositoryManager.getRepository(properties.getProperty("sesame_repository"));
  }

  private void initVirtuosoStore(Properties properties) throws Exception {
    String jDBCconnectionstring = properties.getProperty( "JDBCconnection" );
    String jDBCuser = properties.getProperty( "JDBCuser" );
    String jDBCpassword = properties.getProperty( "JDBCpassword" );
    this.repository = new VirtuosoRepository( jDBCconnectionstring, jDBCuser, jDBCpassword );
  }

  public void addStatements( Model statements, Resource... contexts ) {
    try {
      connection.add( statements, contexts );
    } catch ( RepositoryException e ) {
      throw new IllegalArgumentException( e );
    }
  }

  public QueryResult sparqlSelect( String query ) throws IllegalArgumentException, IllegalStateException {
    Collection<BindingSet> bindings = sparqlRawSelect( query );

    QueryResult results = new QueryResult();

    for ( BindingSet binding : bindings ) {
      Map<String, String> currentRow = new HashMap<String, String>();
      for ( String name : binding.getBindingNames() )
        if ( binding.getValue( name ) != null )
          currentRow.put( name, binding.getValue( name ).stringValue() );
      results.add( currentRow );
    }

    return results;
  }

  /**
   * Performs a SPARQL query on the engine and returns the raw BindingSets.
   *
   * @param query Posed SPARQL query.
   * @return TupleQueryResult coming from the Sparql query.
   * @throws IllegalArgumentException Thrown if the supplied query couldn't be evaluated.
   * @throws IllegalStateException    Thrown if the query wasn't ready for accepting data.
   */
  public List<BindingSet> sparqlRawSelect( String query ) throws IllegalArgumentException, IllegalStateException {
    try {
      TupleQueryResult tupleQueryResult =
          this.connection.prepareTupleQuery( QueryLanguage.SPARQL, query ).evaluate();
      List<BindingSet> bindings = new ArrayList<BindingSet>();

      while ( tupleQueryResult.hasNext() ) {
        BindingSet set = tupleQueryResult.next();
        bindings.add( set );
      }
      return bindings;

    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    } catch ( MalformedQueryException e ) {
      throw new IllegalArgumentException( e );
    } catch ( QueryEvaluationException e ) {
      throw new IllegalArgumentException( e );
    }
  }

  /**
   * Performs a SPARQL query on the engine and returns the raw BindingSets.
   *
   * @param query SPARQL construct query
   * @return Model statements coming from the Sparql query.
   * @throws IllegalArgumentException Thrown if the supplied query couldn't be evaluated.
   * @throws IllegalStateException    Thrown if the query wasn't ready for accepting data.
   */
  public Model sparqlGraphQuery( String query ) throws IllegalArgumentException, IllegalStateException {
    try {
      GraphQueryResult queryResult =
              this.connection.prepareGraphQuery( QueryLanguage.SPARQL, query ).evaluate();
      Model statements = new LinkedHashModel();
      while ( queryResult.hasNext() ) {
        statements.add(queryResult.next());
      }
      queryResult.close();
      return statements;

    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    } catch ( MalformedQueryException e ) {
      throw new IllegalArgumentException( e );
    } catch ( QueryEvaluationException e ) {
      throw new IllegalArgumentException( e );
    }
  }



  public void sparqlUpdate( String query ) throws IllegalArgumentException, IllegalStateException {
    try {
      this.connection.prepareUpdate( QueryLanguage.SPARQL, query ).execute();
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    } catch ( UpdateExecutionException e ) {
      throw new IllegalStateException( e );
    } catch ( MalformedQueryException e ) {
      throw new IllegalArgumentException( e );
    }

  }

  /**
   * returns a fresh uri with the given base and the given kind (base/kind/random) that is not in
   * the sparql endpoint of this engine
   */
  public String provideFreshUri( String base, String kind ) {
    if ( !base.endsWith( "/" ) ) {
      base += "/";
    }
    String start = base;
    if ( kind != null ) {
      if ( !kind.endsWith( "/" ) ) {
        kind += "/";
      }
      start += kind;
    }

    try {
      TupleQueryResult result = connection.prepareTupleQuery( QueryLanguage.SPARQL, "SELECT ?id where { BIND(bif:sequence_next(\"" + start + "\") as ?id) }" ).evaluate();
      return start + result.next().getBinding( "id" ).getValue().stringValue();
    } catch ( Exception e ) {
      try {
        // no bif:sequence_next available (e.g. sesame store used)? go for semi-randomness
        Random randgen = new Random();
        randgen.setSeed( new Date().getTime() );

        String currentTry;
        do {
          currentTry = start + UUID.randomUUID();
          // race condition has a low chance to occur !!
        } while ( hasUri( currentTry ) );

        return currentTry;
      } catch ( Exception e2 ) {
        throw new IllegalStateException( e2 );
      }
    }
  }

  //* returns a fresh uri with the given base that is not in the sparql endpoint of this engine
  public String provideFreshUri( String kind ) {
    return provideFreshUri( Sparql.getClassMapVariable( "DEFAULT_CATALOG" ).toString(), kind );
  }

  //* returns a fresh uri that is not in the sparql endpoint. The uri has the default base
  public String provideFreshUri() {
    return provideFreshUri( null );
  }

  //* whether or not the given uri is used in the sparql; endpoint
  public boolean hasUri( String uri ) {
    try {
      StringBuilder builder = new StringBuilder();

      // this hack seems to circumvent the issue
      builder.append( "SELECT * { { <" ).append( uri ).append( "> ?p ?o. Bind( <" ).append( uri ).append( "> as ?s ). } UNION " );
      builder.append( "{ ?s ?p <" + uri + "> . Bind( <" + uri + "> as ?o ) . } UNION " );
      builder.append( "{ ?s <" + uri + "> ?o . Bind( <" + uri + "> as ?p ) . } UNION " );
      builder.append( "{ GRAPH <" + uri + "> {?s ?p ?o} }} LIMIT 2" );

      return this.connection.prepareTupleQuery( QueryLanguage.SPARQL, builder.toString() ).evaluate().hasNext();
    } catch ( Exception e ) {
      throw new IllegalArgumentException( e );
    }
  }

  //* frees resources
  public void terminate() {
    try {
      this.connection.close();
      this.repository.shutDown();
    } catch ( Exception e ) {
      log.error( "Could not close repository, error message {}", e );
    }
  }

  public void clearGraph( URI graph ) {
    try {
      this.connection.clear( graph );
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    }
  }

  /**
   * Creates a temporary location to store the results of the given sparql construct in. Returns a
   * string that can be
   * used as a pointer for the temporary storage. Any items in the temporary storage will always be
   * included in a
   * graph with the string interpreted as a uri. This graph *MUST* be used when querying the engine
   * with this storage.
   * <p/>
   * This allows complete separation of the logical split of the storages and their physical split
   * <p/>
   * <p/>
   * <b>NOTE The implementation of the sparqlengine will have to ensure that different instances of
   * such a temporary
   * storage can coexist at the same time.</b>
   */
  public String createTemporaryStorage( String sparqlConstruct ) throws Exception {
    int maxRetry = 10;
    int count = maxRetry;
    while ( count > 0 ) {
      try {
        String graphURI = provideFreshUri( "temporary-storage" );
        this.connection.prepareGraphQuery( QueryLanguage.SPARQL, "CREATE GRAPH <" + graphURI + ">" );
        //FIXME if we want a description that this is a temporary graph, maybe use copy instead.
        this.insertConstruct( sparqlConstruct, graphURI );
        return graphURI;
      } catch ( Exception e ) {
        log.warn( "Could not create temporary storage, error: {}", e );
      }
      count--;
    }
    throw new IllegalStateException( "Could not create temporary storage, gave up after " + maxRetry + " times." );
  }

  /**
   * Inserts the given construct query's result into the given graphURI
   */
  public void insertConstruct( String sparqlConstruct, String graphURI ) throws Exception {
    sparqlConstruct = sparqlConstruct.trim();
    if ( !sparqlConstruct.toLowerCase().startsWith( "construct" ) ) {
      throw new IllegalArgumentException( "Could not handlePreCreate query, not a construct query: " + sparqlConstruct );
    }
    int firstWhere = sparqlConstruct.toLowerCase().indexOf( "where" );
    String sparqlInsert = "INSERT { GRAPH <" + graphURI + "> " + sparqlConstruct.substring( 9, firstWhere ) + " } " + sparqlConstruct.substring( firstWhere );
    this.connection.prepareGraphQuery( QueryLanguage.SPARQL, sparqlInsert ).evaluate();
  }

  /**
   * Runs the given query over the normal store in combination with the temporary storage store
   * <p/>
   * This implementation of the sparql engine can simply use the standard sparql select, as the
   * temporary storage is
   * simply a graph in the endpoint
   */
  public QueryResult sparqlSelect( String query, String tempStorage ) {
    return sparqlSelect( query );
  }


  /**
   * Completely removes the temporary storage, returns true upon success.
   */
  public boolean removeTemporaryStorage( String tempStorageHandle ) {
    try {
      this.connection.prepareGraphQuery( QueryLanguage.SPARQL, "DROP SILENT GRAPH <" + tempStorageHandle + ">" ).evaluate();
      return true;
    } catch ( Exception e ) {
      log.error( "Could not remove temporary storage {}, error message: {}.", tempStorageHandle, e );
    }
    return false;
  }

  /**
   * Runs the given sparql construct query and returns a sparql engine over a new non-persistent
   * memory store with only the results of the query
   */
  public SparqlEngine sparqlConstruct( String query ) {
    try {
      GraphQueryResult result = this.connection.prepareGraphQuery( QueryLanguage.SPARQL, query ).evaluate();
      SparqlEngine engine = new SparqlEngine( new SailRepository( new MemoryStore() ) );

      while ( result.hasNext() ) {
        Statement statement = result.next();
        try {
          engine.addStatement( statement );
        } catch ( Exception e ) {
          log.error( "Could not add catalog statement {} to memory store for processing. Error message: {}", statement, e );
        }
      }

      return engine;
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    } catch ( MalformedQueryException e ) {
      throw new IllegalArgumentException( e );
    } catch ( QueryEvaluationException e ) {
      throw new IllegalArgumentException( e );
    }
  }

  /**
   * Runs the given sparql construct query and returns the results in a Model.
   */
  public Model sparqlModelConstruct( String query ) {
    try {
      GraphQueryResult result = this.connection.prepareGraphQuery( QueryLanguage.SPARQL, query ).evaluate();
      Model model = new LinkedHashModel();

      while ( result.hasNext() ) {
        Statement statement = result.next();
        try {
          model.add( statement );
        } catch ( Exception e ) {
          log.error( "Could not add catalog statement {} to memory store for processing. Error message: {}", statement, e );
        }
      }
      return model;
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e );
    } catch ( MalformedQueryException e ) {
      throw new IllegalArgumentException( e );
    } catch ( QueryEvaluationException e ) {
      throw new IllegalArgumentException( e );
    }
  }


  private void addStatement( Statement next ) throws Exception {
    this.connection.add( next );
  }

  public String buildLanguageVariable( String datasetLangPredicate ) {
    return "_".concat( datasetLangPredicate ).concat( "_Lang" );
  }

  private Map<String, Map<String, Set<Value>>> statements;

  public Map<String, Map<String, Set<Value>>> getStatements() {
    if ( this.statements != null ) {
      return this.statements;
    }
    try {
      RepositoryResult<Statement> statements = this.connection.getStatements( null, null, null, true );
      Map<String, Map<String, Set<Value>>> result = new HashMap<String, Map<String, Set<Value>>>();
      while ( statements.hasNext() ) {
        Statement statement = statements.next();
        String subject = statement.getSubject().stringValue();
        Map<String, Set<Value>> predicates = result.get( subject );
        if ( predicates == null ) {
          predicates = new HashMap<String, Set<Value>>();
          result.put( subject, predicates );
        }
        String predicate = statement.getPredicate().stringValue();
        Set<Value> objects = predicates.get( predicate );
        if ( objects == null ) {
          objects = new HashSet<Value>();
          predicates.put( predicate, objects );
        }

        Value object = statement.getObject();
        objects.add( object );
      }
      this.statements = result;
      return result;
    } catch ( Exception e ) {
      log.error( "Could not fetch statements from the current endpoint: {}. Error message: {}", this.repository, e );
      return new HashMap<String, Map<String, Set<Value>>>();
    }
  }

  /**
   * Returns the string value of the statement in the given statements list. Assumes single value
   */
  public static String stringValue( Map<String, Map<String, Set<Value>>> statements, String subject, String predicate ) {
    try {
      return statements.get( subject ).get( predicate ).iterator().next().stringValue();
    } catch ( Exception e ) {
      return null;
    }
  }

  /**
   * Returns the objects for the given subject and predicate as a list of strings
   */
  public static List<String> stringList( Map<String, Map<String, Set<Value>>> statements, String subject, String predicate ) {
    ArrayList<String> result = new ArrayList<String>();
    for ( Value value : statements.get( subject ).get( predicate ) ) {
      result.add( value.stringValue() );
    }
    return result;
  }

  public Model getStatements( Resource subject, URI predicate, Value value, boolean includeInferred, Resource... contexts ) {
    try {
      RepositoryResult<Statement> statements = this.connection.getStatements( subject, predicate, value, includeInferred, contexts );
      Model model = new LinkedHashModel( Iterations.asList( statements ) );
      statements.close();
      return model;
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e.getCause() );
    }
  }

  public Model getStatements( Resource... contexts ) {
    try {
      RepositoryResult<Statement> statements = this.connection.getStatements( null, null, null, true, contexts );
      Model model = new LinkedHashModel( Iterations.asList( statements ) );
      statements.close();
      return model;
    } catch ( RepositoryException e ) {
      throw new IllegalStateException( e.getCause() );
    }
  }


  public static Integer intValue( Map<String, Map<String, Set<Value>>> statements, String subject, String predicate ) {
    Iterator<Value> iterator = statements.get( subject ).get( predicate ).iterator();
    if ( !iterator.hasNext() ) {
      return null;
    }
    String intString = iterator.next().stringValue();
    try {
      return Integer.parseInt( intString );
    } catch ( Exception e ) {
//      log.error("Could not parse date {} from sparql result s: {} p: {} . Error: {}", intString, subject, predicate, e);
      return null;
    }
  }

  public boolean hasStatement(Resource subject, URI predicate, Value value,boolean includeRef, Resource...contexts) {
    try {
      return this.connection.hasStatement(subject, predicate, value, includeRef, contexts);
    } catch (RepositoryException e) {
      throw new IllegalStateException(e);
    }
  }
}
