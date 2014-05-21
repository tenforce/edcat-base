package eu.lod2.edcat.utils;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.memory.MemoryStore;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper for constructing a temporary repository with Sparql capabilities.
 */
public class TemporaryRepository {

  /**
   * The repository used for temporarily storing the triples.
   */
  private SailRepository tmpRepository;
  /**
   * Connection for temporarily storing the triples through.
   */
  private SailRepositoryConnection tmpRepositoryConnection;

  /**
   * Constructs and prepares a new TemporaryRepository for access.
   *
   * @see #tearDown()
   */
  public TemporaryRepository() throws RepositoryException {
    setup();
  }

  /**
   * This method is called automatically when you construct a new TemporaryRepository.
   * <p/>
   * In order to put the TemporaryRepository in a working state, this method should be called. After
   * you're done with the TemporaryRepository, {@link #tearDown} should be called.
   *
   * @throws RepositoryException Thrown when the repository could not be initialized.
   * @see #tearDown()
   */
  public void setup() throws RepositoryException {
    this.tmpRepository = new SailRepository( new MemoryStore() );
    this.tmpRepository.initialize();
    this.tmpRepositoryConnection = this.tmpRepository.getConnection();
  }

  /**
   * Called to free up the resources regarding this TemporaryRepository.
   * <p/>
   * This *must* be called before garbage collection occurs.
   *
   * @throws RepositoryException Thrown when the repository failed to shut down.
   */
  public void tearDown() throws RepositoryException {
    tmpRepositoryConnection.close();
    tmpRepository.shutDown();
    tmpRepository = null;
    tmpRepositoryConnection = null;
  }

  /**
   * Cleans the repository so it is ready for accepting new values.
   *
   * @throws RepositoryException Thrown when the repository failed to shut down or set up.
   */
  @SuppressWarnings( "UnusedDeclaration" )
  public void clean() throws RepositoryException {
    // todo: this could clean the data and reuse the rest of the repository and the connection.
    tearDown();
    setup();
  }

  /**
   * @see org.openrdf.repository.sail.SailRepositoryConnection#add(org.openrdf.model.Statement,
   * org.openrdf.model.Resource...)
   */
  public void add( Statement s, Resource... contexts ) throws RepositoryException {
    tmpRepositoryConnection.add( s, contexts );
  }

  /**
   * @see org.openrdf.repository.sail.SailRepositoryConnection#add(Iterable,
   * org.openrdf.model.Resource...)
   */
  public void add( Iterable<? extends Statement> statements, Resource... contexts ) throws RepositoryException {
    tmpRepositoryConnection.add( statements, contexts );
  }

  /**
   * Executes a SPARQL query on this repository.
   *
   * @param query String representation of the SPARQL query which should be executed.
   * @return QueryResults containing the results of executing the SPARQL queries on the graph in
   * this TemporaryRepository.
   */
  public QueryResult sparqlQuery( String query ) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
    TupleQueryResult sparqlResult = this.tmpRepositoryConnection.prepareTupleQuery( QueryLanguage.SPARQL, query ).evaluate();

    // convert sparqlResult to QueryResult
    QueryResult queryResult = new QueryResult();
    while ( sparqlResult.hasNext() ) {
      BindingSet set = sparqlResult.next();
      Map<String, String> currentRow = new HashMap<String, String>();
      for ( String name : set.getBindingNames() )
        currentRow.put( name, set.getValue( name ).stringValue() );
      queryResult.add( currentRow );
    }

    return queryResult;
  }

}